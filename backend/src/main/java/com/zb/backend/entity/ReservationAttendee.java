package com.zb.backend.entity;

import lombok.Data;

@Data
public class ReservationAttendee {
    private Integer id;
    private Long reservationId;
    private Long accountId;
    private String empName;
}
