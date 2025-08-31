package com.zb.backend.service;

import com.zb.backend.constants.enums.ReservationEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.constants.enums.RoomAvailabilityEnum;
import com.zb.backend.entity.*;
import com.zb.backend.entity.enums.NotificationType;
import com.zb.backend.entity.enums.ReservationStatus;
import com.zb.backend.entity.enums.RoomStatus;
import com.zb.backend.entity.enums.RoomType;
import com.zb.backend.mapper.ReservationMapper;
import com.zb.backend.model.JwtClaim;
import com.zb.backend.model.PageResult;
import com.zb.backend.model.TimeSlot;
import com.zb.backend.model.request.QueryReservationRequest;
import com.zb.backend.model.request.ReservationRequest;
import com.zb.backend.model.request.UpdateReservationRequest;
import com.zb.backend.model.response.SimpleDeptEmpResponse;
import com.zb.backend.util.PaginationValidator;
import com.zb.backend.util.RollBackAvailStatusUtil;
import com.zb.backend.util.RoomTimeSlotUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationMapper reservationMapper;
    private final MeetingRoomService meetingRoomService;
    private final EmployeeService employeeService;
    private final RoomAvailabilityService roomAvailabilityService;
    private final ReservationAttendeeService reservationAttendeeService;
    private final NotificationService notificationService;
    private final RollBackAvailStatusUtil rollBackAvailStatusUtil;

    // 预约会议室
    @Transactional(rollbackFor = Exception.class)
    public ResultEnum reservation(@Valid ReservationRequest reservationRequest, JwtClaim jwtClaim) {
        // 判断是否为管理员
        Boolean isAdmin = jwtClaim.getIsAdmin();
        if (isAdmin) throw new RuntimeException(ReservationEnum.ERR_RES_ADMIN.getMessage());
        // 首先查找会议室是否存在
        Long roomId = reservationRequest.getRoomId();
        MeetingRoom meetingRoom = meetingRoomService.getRoomByRoomId(roomId);
        if (meetingRoom == null) throw new RuntimeException(ReservationEnum.ERR_EXISTS_ROOM.getMessage());
        // 判断会议室状态是否启用
        if (meetingRoom.getRoomStatus() != RoomStatus.AVAILABLE) throw new RuntimeException(ReservationEnum.ERR_STATUS_ROOM.getMessage());
        // 通过JwtClaim获取accountId查询员工信息
        Long accountId = jwtClaim.getAccountId();
        Employee emp = employeeService.getEmployeeByEmpId(accountId);
        // 判断会议室类型，是否为员工可选
        if (meetingRoom.getRoomType() == RoomType.LARGE && !emp.getIsManager()) throw new RuntimeException(ReservationEnum.ERR_NOT_MANAGER_ROOM.getMessage());
        // 判断是否@全体员工
        if (reservationRequest.getMentionAll() && !emp.getIsManager()) throw new RuntimeException(ReservationEnum.ERR_NOT_MANAGER_MENTION.getMessage());
        // 获取日期、开始时间、结束时间，判断时间段是否合法
        LocalDate reservationDate = reservationRequest.getReservationDate();
        LocalTime startTime = reservationRequest.getStartTime();
        LocalTime endTime = reservationRequest.getEndTime();
        if (startTime.isAfter(endTime)) throw new RuntimeException(ReservationEnum.ERR_TIME_SLOT.getMessage());
        // 添加一个约束：判断预约时间段是否正确，只判断开始时间是否＜结束时间，好吧，字符串判断有点麻烦
        // 根据roomId和date获取int状态值
        RoomAvailability roomAvailability = roomAvailabilityService.selectRoomAvailStatus(roomId, reservationDate);
        if (roomAvailability == null) throw new  RuntimeException(ReservationEnum.ERR_EXISTS_DATE.getMessage());
        Integer oldStatus = roomAvailability.getSlotStatus();
        // 通过工具类处理，得到新的状态值
        Integer newStatus = RoomTimeSlotUtil.convertTimeSlotToStatus(new TimeSlot(startTime.toString(), endTime.toString()));
        // 两个状态值做“与”运算，判断结果是否为0
        Integer statusCompare = oldStatus & newStatus;
        System.out.println(oldStatus + " & " + newStatus + " = " + statusCompare);
        if (statusCompare != 0) throw new RuntimeException(ReservationEnum.ERR_RES_TIME_CONFLICT.getMessage());
        // 将新旧状态进行“或”运算，得到添加时间段后的状态
        Integer statusResult = oldStatus | newStatus;
        System.out.println(oldStatus + "｜" + newStatus + " = " + statusResult);
        // 第一次操作数据库：会议室状态表
        Boolean updateStatus = roomAvailabilityService.updateStatusByIdAndDate(roomId, reservationDate, statusResult);
        if (!updateStatus) throw new RuntimeException(ReservationEnum.ERR_UPDATE_STATUS.getMessage());
        // 获取预约员工姓名
        String empName = emp.getEmpName();
        // 第二次操作数据库：创建一条预约数据
        Boolean insertReservation = reservationMapper.insertReservation(reservationRequest, accountId, empName);
        if (!insertReservation) throw new RuntimeException(ReservationEnum.ERR_RESERVATION.getMessage());
        // 通过roomId、accountId、date、time，获取刚刚的预约id
        Reservation reservation = reservationMapper.selectReservation(roomId, accountId, reservationDate, startTime, endTime);
        Long reservationId = reservation.getReservationId();
        // 获取参会人员ids，并将预约人也添加进去
        List<Long> attendees = reservationRequest.getAttendees();
        attendees.add(accountId);
        // 如果@全体成员，获取同部门员工信息，并赋值到ids中，其实简单部门员工信息是有姓名的，有点多此一举了，以后优化
        if (reservationRequest.getMentionAll()) {
            // 获取同部门员工信息，将集合中的员工
            List<SimpleDeptEmpResponse> simpleDeptEmp = employeeService.getSimpleDeptEmp(jwtClaim);
            // 遍历员工集合，提取empId并添加到attendees中
            for (SimpleDeptEmpResponse deptEmp : simpleDeptEmp) {
                // 确保empId不为null再添加（可选，根据实际业务判断）
                if (deptEmp.getEmpId() != null) {
                    attendees.add(deptEmp.getEmpId());
                }
            }
        }
        System.out.println("预约id：" + reservationId + " 参会人员：" + attendees);
        // 遍历集合，批量进行添加操作
        for (Long attendeeId : attendees) {
            System.out.println("当前进行操作的员工id：" + attendeeId);
            // 首先根据id，查询员工姓名
            Employee attendeeEmp = employeeService.getEmployeeByEmpId(attendeeId);
            // 这里需要进行两个判断：判断id是否存在，判断是否同部门人员
            if (attendeeEmp == null) throw new RuntimeException(ReservationEnum.ERR_EXISTS_EMP.getMessage() + attendeeId);
            if (!emp.getDeptId().equals(attendeeEmp.getDeptId())) throw new RuntimeException(ReservationEnum.ERR_DEPT_EMP.getMessage() + attendeeId);
            // 会议室参与表需要传入的字段：预约ID、账号ID、员工姓名
            // 批量插入会议室参与
            Boolean insertAttendee = reservationAttendeeService.addAttendee(reservationId, attendeeId, attendeeEmp.getEmpName());
            if (!insertAttendee) throw new RuntimeException(ReservationEnum.ERR_ROOM_ATTENDEE.getMessage());
        }
        // 通知需要传入的字段：发送者ID，接收者ID，标题，内容，关联预约ID
        Long senderId = 101L;
        String title = "预约通知";
        String content = "您选择的会议室" + roomId + "，日期：" + reservationDate + "，时间段[" + startTime + "—" + endTime + "]，已提交预约，请等待审核";
        String notificationType = "SYSTEM";
        // 给预约人发送预约成功通知
        Boolean insertNotify = notificationService.addNotification(senderId, accountId, notificationType, title, content, reservationId);
        if (!insertNotify) throw new RuntimeException(ReservationEnum.ERR_NOTIFICATION.getMessage());

        return ReservationEnum.SUC_RESERVATION;

    }

    // 分页查询预约信息
    public PageResult<Reservation> queryReservations(@Valid QueryReservationRequest request, JwtClaim jwtClaim) {
        // 通过isAdmin判断是否为管理员，不是按照用户id查询，否则查全部
        Boolean isAdmin = jwtClaim.getIsAdmin();
        Long accountId = jwtClaim.getAccountId();
        if (isAdmin) accountId = null;
        request.setAccountId(accountId);

        // 首先查询总行数，在执行分页参数校验，最后查询数据，并返回

        // 查询总记录数
        Integer total = reservationMapper.countReservationList(request);

        // 分页校验工具类
        PageResult<Reservation> pageResult = PaginationValidator.validatePagination(request, total);
        if (pageResult != null) {
            return pageResult;
        }

        // 记录数不为0，根据筛选条件查询
        List<Reservation> reservationList = reservationMapper.selectReservationList(request);

        return new PageResult<>(total, request.getPageNum(), request.getPageSize(), reservationList);

    }

    // 修改会议室状态
    @Transactional(rollbackFor = Exception.class)
    public ResultEnum updateReservation(@Valid UpdateReservationRequest request, JwtClaim jwtClaim) {
        // 获取预约id
        Long reservationId = request.getReservationId();
        // 判断预约记录是否存在
        Reservation reservation = reservationMapper.selectReservationById(reservationId);
        if (reservation == null) throw new RuntimeException(ReservationEnum.ERR_EXISTS_RES.getMessage());

        // 获取旧预约信息
        String  oldStatus = reservation.getReservationStatus().toString();
        LocalDate  reservationDate = reservation.getReservationDate();
        String startTime = reservation.getStartTime().toString();
        String endTime = reservation.getEndTime().toString();
        Long resAccId = reservation.getAccountId();
        // String empName = reservation.getEmpName();
        Long roomId = reservation.getRoomId();

        // 获取isAdmin和accountId
        Boolean isAdmin = jwtClaim.getIsAdmin();
        Long accountId = jwtClaim.getAccountId();

        // 当为员工时，判断是否为自己的预约
        if (!isAdmin && !accountId.equals(resAccId)) throw new RuntimeException(ReservationEnum.ERR_ACC_RES.getMessage());

        // 获取状态
        String resStatus = request.getReservationStatus();
        // 只有 待审核 已通过 两种状态能修改
        if (!"PENDING".equals(oldStatus) &&
                !"APPROVED".equals(oldStatus)) throw new RuntimeException(ReservationEnum.ERR_STATUS_UPDATE.getMessage());

        // 现在只剩下两个状态需要判断了，管理员只能在 待审核 状态下进行修改，员工可以在这两种情况下 取消
        if (isAdmin && "APPROVED".equals(oldStatus)) throw new RuntimeException(ReservationEnum.ERR_STATUS_UPDATE.getMessage());
        // 员工无需判断，当前两种状态都能取消
        // 接下来判断请求状态，管理员能 通过 拒绝 ，员工只能 取消
        if (isAdmin && "CANCELLED".equals(resStatus)) throw new RuntimeException(ReservationEnum.ERR_ADMIN_CANCELLED.getMessage());
        if (!isAdmin && !"CANCELLED".equals(resStatus)) throw new RuntimeException(ReservationEnum.ERR_EMP_CANCELLED.getMessage());

        // 账号类型操作判断完成，接下来根据账号类型，来进行后续操作
        // 当为管理员操作时
        if (isAdmin) {
            // 如果执行的是 通过 操作
            if ("APPROVED".equals(resStatus)) {
                // 通过 逻辑：将预约状态改为通过，设置拒绝原因为空，设置审核人id为管理员
                request.setRejectReason(null);
                request.setApprovalAccountId(accountId);
                Boolean updateRes = reservationMapper.updateReservation(request);
                if (!updateRes) throw new RuntimeException(ReservationEnum.ERR_UPDATE_RES.getMessage());
                // 修改成功，给关联该预约的员工发送审核通知
                // 根据预约id获取员工集合
                List<ReservationAttendee> attendees = reservationAttendeeService.getAttendeeByResId(reservationId);

                // 通知需要传入的字段：发送者ID，接收者ID，标题，内容，关联预约ID
                String title = "审核通知";
                String content = reservation.toApprovedInvitationString();
                String notificationType = NotificationType.APPROVAL.toString();

                for (ReservationAttendee attendee : attendees) {
                    System.out.println("当前进行操作的员工id：" + attendee.getAccountId());
                    // 给关联的所有员工发送通知
                    Boolean insertNotify = notificationService.addNotification(accountId, attendee.getAccountId(), notificationType, title, content, reservationId);
                    if (!insertNotify) throw new RuntimeException(ReservationEnum.ERR_INSERT_NOTIFY.getMessage());
                }


            }

            // 如果执行的是 拒绝 操作
            if ("REJECTED".equals(resStatus)) {
                // 判断拒绝原因是否为空
                if (request.getRejectReason() == null) throw new RuntimeException(ReservationEnum.ERR_NULL_REASON.getMessage());
                // 首先要回退预约，然后修改预约表，并设置拒绝信息，最后再给预约人发通知
                // 回退预约，根据开始时间和结束时间，调用工具类，获取状态值，然后按位取反，再与数据库中的状态值做 与 运算，得到新值，并插入
                // 通过工具类获取状态值
                // Integer cancelledRawStatus = RoomTimeSlotUtil.convertTimeSlotToStatus(new TimeSlot(startTime, endTime));
                // Integer cancelledStatus = ~cancelledRawStatus;
                // System.out.println("取消预约原状态：" + cancelledRawStatus + "-取反后：" + cancelledStatus);
                // // 通过预约id获取旧的状态值
                // RoomAvailability roomAvailability = roomAvailabilityService.selectRoomAvailStatus(roomId, reservationDate);
                // Integer oldAvailStatus = roomAvailability.getSlotStatus();
                // // 两个状态值按位 与 得到新的状态值
                // Integer newAvailStatus = cancelledStatus & oldAvailStatus;
                // System.out.println("旧的状态值：" + oldAvailStatus + " 按位与得到的新的状态值" + newAvailStatus);
                // // 将新的状态值插入状态表中
                // Boolean updateAvailStatus = roomAvailabilityService.updateStatusByIdAndDate(roomId, reservationDate, newAvailStatus);
                // if (!updateAvailStatus) throw new RuntimeException("更新状态值异常");
                // // 修改预约状态
                // request.setApprovalAccountId(accountId);
                // Boolean updateRes = reservationMapper.updateReservation(request);
                // if (!updateRes) throw new RuntimeException("修改失败");
                // // 给预约人发通知
                // // 通知需要传入的字段：发送者ID，接收者ID，标题，内容，关联预约ID
                // String title = "拒绝通知";
                // String content = "您选择的会议室" + roomId + "，日期：" + reservationDate + "，时间段[" + startTime + "—" + endTime + "]，已被拒绝，处理人：" + accountId;
                // String notificationType = NotificationType.REJECTION.toString();
                // // 给预约人发送预约成功通知
                // Boolean insertNotify = notificationService.addNotification(accountId, resAccId, notificationType, title, content, reservationId);
                // if (!insertNotify) throw new RuntimeException(ReservationEnum.ERR_NOTIFICATION.getMessage());

                request.setApprovalAccountId(accountId);
                Boolean updateRes = reservationMapper.updateReservation(request);
                if (!updateRes) throw new RuntimeException(ReservationEnum.ERR_UPDATE_RES.getMessage());

                String title = "拒绝通知";
                String content = "拒绝";
                String notificationType = NotificationType.REJECTION.toString();
                Boolean isSuc = rollBackAvailStatusUtil.RollBackAvailStatus(reservation, accountId, request, accountId, title, content, notificationType);

                if (!isSuc) throw new RuntimeException(ReservationEnum.ERR_UPDATE_RES.getMessage());
            }
        }

        // 如果当前状态为 已审核 员工执行 取消 操作，额外需要给参会人员发通知


        if (!isAdmin && "CANCELLED".equals(resStatus)) {
            // 如果用户执行 取消 操作
            // 首先要回退预约，然后修改预约表，并设置拒绝信息，最后再给预约人发通知
            // 回退预约，根据开始时间和结束时间，调用工具类，获取状态值，然后按位取反，再与数据库中的状态值做 与 运算，得到新值，并插入
            // 通过工具类获取状态值

            // 将请求体内的拒绝原因和处理人id设为null


            // 直接调用回退方法，传值并发消息

            // 修改预约状态，将请求体内的拒绝原因设为null
            request.setRejectReason(null);
            request.setApprovalAccountId(101L);
            Boolean updateRes = reservationMapper.updateReservation(request);
            if (!updateRes) throw new RuntimeException(ReservationEnum.ERR_UPDATE_RES.getMessage());

            String title = "取消通知";
            String content = "取消";
            String notificationType = NotificationType.SYSTEM.toString();
            Boolean isSuc = rollBackAvailStatusUtil.RollBackAvailStatus(reservation, 101L, request, 101L, title, content, notificationType);

            if (!isSuc) throw new RuntimeException(ReservationEnum.ERR_UPDATE_RES.getMessage());

        }



        return ReservationEnum.SUC_UPDATE_RES;
    }
}
