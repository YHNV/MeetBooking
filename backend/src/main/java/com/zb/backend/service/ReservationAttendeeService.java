package com.zb.backend.service;

import com.zb.backend.entity.ReservationAttendee;
import com.zb.backend.mapper.ReservationAttendeeMapper;
import com.zb.backend.model.response.SimpleDeptEmpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationAttendeeService {
    private final ReservationAttendeeMapper reservationAttendeeMapper;

    // 插入预约参与人员
    public Boolean addAttendee(Long reservationId, Long accountId, String empName) {
        return reservationAttendeeMapper.insertAttendee(reservationId, accountId, empName);
    }

    // 根据预约id获取参会员工集合
    public List<ReservationAttendee> getAttendeeByResId(Long reservationId) {
        return reservationAttendeeMapper.selectAttendeeList(reservationId);
    }
}
