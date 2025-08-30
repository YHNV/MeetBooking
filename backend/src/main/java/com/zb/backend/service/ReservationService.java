package com.zb.backend.service;

import com.zb.backend.constants.enums.ReservationEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.constants.enums.RoomAvailabilityEnum;
import com.zb.backend.entity.Employee;
import com.zb.backend.entity.MeetingRoom;
import com.zb.backend.entity.Reservation;
import com.zb.backend.entity.RoomAvailability;
import com.zb.backend.entity.enums.RoomStatus;
import com.zb.backend.entity.enums.RoomType;
import com.zb.backend.mapper.ReservationMapper;
import com.zb.backend.model.JwtClaim;
import com.zb.backend.model.PageResult;
import com.zb.backend.model.TimeSlot;
import com.zb.backend.model.request.QueryReservationRequest;
import com.zb.backend.model.request.ReservationRequest;
import com.zb.backend.model.response.SimpleDeptEmpResponse;
import com.zb.backend.util.PaginationValidator;
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
}
