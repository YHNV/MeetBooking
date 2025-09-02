package com.zb.backend.service.task;

import com.zb.backend.entity.Reservation;
import com.zb.backend.entity.enums.NotificationType;
import com.zb.backend.mapper.NotificationMapper;
import com.zb.backend.service.ReservationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationExpireTaskService {


    private final ReservationService reservationService;
    private final NotificationMapper notificationMapper;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void reservationExpire() {
        // 获取当前日期（例如：9月3日0点执行时，当前日期是9月3日）
        LocalDate currentDate = LocalDate.now();
        System.out.println("当前时间为：" + currentDate);
        // 通过日期查询预约记录
        List<Reservation> reservationList = reservationService.getNotExpiredByDate(currentDate);

        String notificationType = NotificationType.SYSTEM.toString();
        String title = "预约过期通知";
        String content = "";

        // 遍历这个集合，并给未审核的预约记录发通知
        for (Reservation reservation : reservationList) {
            String oldStatus = reservation.getReservationStatus().toString();
            Long reservationId = reservation.getReservationId();
            Long accountId = reservation.getAccountId();
            Long roomId = reservation.getRoomId();
            String reservationDate = reservation.getReservationDate().toString();
            String startTime = reservation.getStartTime().toString();
            String endTime = reservation.getEndTime().toString();

            // 修改这些预约记录为过期
            // EXPIRED
            Boolean updateExpired = reservationService.updateExpiredById(reservationId);
            if (!updateExpired) throw new RuntimeException("修改过期错误");
            // 如果原来的状态是未审核，给预约人发送一条信息
            if (!oldStatus.equals("APPROVED")) {
                content = "您预约的会议室" + roomId + "，日期：" + reservationDate + "，时间段[" + startTime + "—" + endTime + "]，已过期";
                // 给预约人发送消息
                Boolean insertNotify = notificationMapper.insertNotification(101L, accountId, notificationType, title, content, reservationId);
                if (!insertNotify) throw new RuntimeException("通知异常提醒");
            }
        }
    }

    @PostConstruct
    public void init() {
        reservationExpire();
    }
}
