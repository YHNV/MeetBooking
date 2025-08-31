package com.zb.backend.util;

import com.zb.backend.constants.enums.ReservationEnum;
import com.zb.backend.entity.Reservation;
import com.zb.backend.entity.ReservationAttendee;
import com.zb.backend.entity.RoomAvailability;
import com.zb.backend.entity.enums.NotificationType;
import com.zb.backend.mapper.NotificationMapper;
import com.zb.backend.mapper.ReservationAttendeeMapper;
import com.zb.backend.mapper.ReservationMapper;
import com.zb.backend.mapper.RoomAvailabilityMapper;
import com.zb.backend.model.TimeSlot;
import com.zb.backend.model.request.UpdateReservationRequest;
import com.zb.backend.service.ReservationService;
import com.zb.backend.service.RoomAvailabilityService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class RollBackAvailStatusUtil {
    private final RoomAvailabilityMapper roomAvailabilityMapper;
    private final NotificationMapper notificationMapper;
    private final ReservationAttendeeMapper reservationAttendeeMapper;

    public RollBackAvailStatusUtil(RoomAvailabilityMapper roomAvailabilityMapper, NotificationMapper notificationMapper, ReservationAttendeeMapper reservationAttendeeMapper) {
        this.roomAvailabilityMapper = roomAvailabilityMapper;
        this.notificationMapper = notificationMapper;
        this.reservationAttendeeMapper = reservationAttendeeMapper;
    }

    // 这个类是用于回退会议室可用状态

    public Boolean RollBackAvailStatus(
            Reservation reservation,
            Long approvalAccountId,
            UpdateReservationRequest request,
            Long senderId,
            String title,
            String content,
            String notificationType) {
        // 首先根据reservationId，查询到预约记录
        // Reservation reservation = reservationMapper.selectReservationById(reservationId);

        // 创建属性赋值
        Long reservationId = reservation.getReservationId();
        LocalDate reservationDate = reservation.getReservationDate();
        String startTime = reservation.getStartTime().toString();
        String endTime = reservation.getEndTime().toString();
        Long resAccId = reservation.getAccountId();
        Long roomId = reservation.getRoomId();
        String resStatus = reservation.getReservationStatus().toString();

        // 将该预约信息中的预约时间段，转换为会议室可用状态值
        Integer rollBackRawStatus = RoomTimeSlotUtil.convertTimeSlotToStatus(new TimeSlot(startTime, endTime));
        Integer rollBackStatus = 0xFFFF ^ rollBackRawStatus;
        System.out.println("取消预约原状态：" + rollBackRawStatus + "-取反后：" + rollBackStatus);
        // 通过预约id获取旧的状态值
        RoomAvailability roomAvailability = roomAvailabilityMapper.selectRoomAvailStatus(roomId, reservationDate);
        Integer oldAvailStatus = roomAvailability.getSlotStatus();
        // 两个状态值按位 与 得到新的状态值
        Integer newAvailStatus = rollBackStatus & oldAvailStatus;
        System.out.println("旧的状态值：" + oldAvailStatus + " 按位与得到的新的状态值" + newAvailStatus);
        // 将新的状态值插入状态表中
        Boolean updateAvailStatus = roomAvailabilityMapper.updateStatusByIdAndDate(roomId, reservationDate, newAvailStatus);
        if (!updateAvailStatus) throw new RuntimeException("更新状态值异常");
        
        // 修改预约状态
        // request.setApprovalAccountId(approvalAccountId);
        // Boolean updateRes = reservationMapper.updateReservation(request);
        // if (!updateRes) throw new RuntimeException("修改失败");

        // 发送通知的时候需要判断原 预约状态 是否为 已审核，如果为 已审核，那么就需要把参与这个预约的人员查询出来，并批量通知
        if ("APPROVED".equals(resStatus)) {
            String cancelContent = "您受邀参与的会议室已被取消；会议室" + roomId + "，日期：" + reservationDate + "，时间段[" + startTime + "—" + endTime + "]"+ "，处理人：" + senderId;
            // 根据预约id获取集合
            List<ReservationAttendee> attendees = reservationAttendeeMapper.selectAttendeeList(reservationId);
            for (ReservationAttendee attendee : attendees) {
                Boolean insertNotify = notificationMapper.insertNotification(senderId, attendee.getAccountId(), notificationType, title, cancelContent, request.getReservationId());
                if (!insertNotify) throw new RuntimeException("通知异常提醒");
            }
            return true;
        }
        
        // 给预约人发通知
        // 通知需要传入的字段：发送者ID，接收者ID，标题，内容，关联预约ID
        // String title = "拒绝通知";
        String rawContent = "您选择的会议室" + roomId + "，日期：" + reservationDate + "，时间段[" + startTime + "—" + endTime + "]，已被" + content + "，处理人：" + senderId;
        // String notificationType = NotificationType.REJECTION.toString();
        // 给预约人发送预约成功通知
        Boolean insertNotify = notificationMapper.insertNotification(senderId, resAccId, notificationType, title, rawContent, request.getReservationId());
        if (!insertNotify) throw new RuntimeException("通知异常提醒");

        return true;
    }

}
