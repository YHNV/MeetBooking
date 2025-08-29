package com.zb.backend.service;

import com.zb.backend.mapper.ReservationAttendeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationAttendeeService {
    private final ReservationAttendeeMapper reservationAttendeeMapper;

    // 插入预约参与人员
    public Boolean addAttendee(Long reservationId, Long accountId, String empName) {
        return reservationAttendeeMapper.insertAttendee(reservationId, accountId, empName);
    }
}
