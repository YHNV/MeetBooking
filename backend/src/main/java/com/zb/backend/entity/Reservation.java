package com.zb.backend.entity;

import com.zb.backend.entity.enums.ReservationStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Data
public class Reservation {
    private Long reservationId;
    private Long roomId;
    private Long accountId;
    private String empName;
    private String meetingTopic;
    private String meetingDesc;
    private LocalDate reservationDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private ReservationStatus reservationStatus;
    private Long approvalAccountId;
    private LocalDateTime approvalTime;
    private String rejectReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /**
     * 审核通过时生成的会议邀请字符串
     * @return 格式化的邀请信息
     */
    public String toApprovedInvitationString() {
        return String.format(
                "您被受邀参与会议：日期%s，时间段[%s-%s]，在%d会议室；会议主题：%s；发起人：%s",
                reservationDate,
                startTime,
                endTime,
                roomId,
                meetingTopic,
                empName
        );
    }
}
