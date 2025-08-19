package com.zb.backend.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RoomAvailability {
    private Long roomId;
    private LocalDate scheduleDate;
    private Integer slotStatus;
}
