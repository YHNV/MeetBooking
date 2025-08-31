package com.zb.backend.mapper;

import com.zb.backend.entity.ReservationAttendee;
import com.zb.backend.model.response.SimpleDeptEmpResponse;

import java.util.List;

public interface ReservationAttendeeMapper {
    // 添加预约参与人员
    Boolean insertAttendee(Long reservationId, Long accountId, String empName);

    // 根据预约id获取参与员工信息
    List<ReservationAttendee> selectAttendeeList(Long reservationId);
}
