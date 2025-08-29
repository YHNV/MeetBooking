package com.zb.backend.mapper;

public interface ReservationAttendeeMapper {
    // 添加预约参与人员
    Boolean insertAttendee(Long reservationId, Long accountId, String empName);
}
