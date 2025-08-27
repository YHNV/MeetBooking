package com.zb.backend.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class GetRoomAvailDateListResponse {
    private Long roomId;
    List<LocalDate> dates;
}
