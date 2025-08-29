package com.zb.backend.mapper;

import com.zb.backend.entity.Reservation;
import com.zb.backend.model.request.ReservationRequest;
import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ReservationMapper {
    // 创建一条预约记录
    Boolean insertReservation(@Valid ReservationRequest reservationRequest, Long accountId, String empName);

    // 通过条件获取Reservation对象
    Reservation selectReservation(Long roomId, Long accountId, LocalDate reservationDate, LocalTime startTime, LocalTime endTime);
}
