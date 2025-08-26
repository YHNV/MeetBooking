package com.zb.backend.model.response;

import lombok.Data;

@Data
public class RoomAvailDateResponse {
    private Long roomId;
    private String startDate;
    private String endDate;
    private String[] dates;
}
