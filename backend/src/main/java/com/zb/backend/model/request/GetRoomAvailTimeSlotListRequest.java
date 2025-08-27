package com.zb.backend.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GetRoomAvailTimeSlotListRequest {
    @NotNull
    private Long roomId;
    @NotNull
    private LocalDate date;
}
