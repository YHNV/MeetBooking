package com.zb.backend.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetRoomAvailTimeSlotListRequest {
    @NotNull
    private Long roomId;
    @NotNull
    private LocalDate date;
}
