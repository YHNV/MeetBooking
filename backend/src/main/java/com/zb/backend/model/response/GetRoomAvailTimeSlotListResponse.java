package com.zb.backend.model.response;

import com.zb.backend.model.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetRoomAvailTimeSlotListResponse {
    private Long roomId;
    private LocalDate date;
    private Integer status;
    List<TimeSlot> timeSlotList;
}
