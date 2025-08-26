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
}
