package com.zb.backend.mapper;

import com.zb.backend.entity.Reservation;
import com.zb.backend.model.request.QueryReservationRequest;
import com.zb.backend.model.request.ReservationRequest;
import com.zb.backend.model.request.UpdateReservationRequest;
import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationMapper {
    // 创建一条预约记录
    Boolean insertReservation(@Valid ReservationRequest reservationRequest, Long accountId, String empName);

    // 通过条件获取Reservation对象
    Reservation selectReservation(Long roomId, Long accountId, LocalDate reservationDate, LocalTime startTime, LocalTime endTime);

    // 查询总行数
    Integer countReservationList(@Valid QueryReservationRequest request);

    // 查询预约信息
    List<Reservation> selectReservationList(@Valid QueryReservationRequest request);

    // 根据预约id查询预约记录
    Reservation selectReservationById(Long reservationId);

    // 修改预约状态
    Boolean updateReservation(@Valid UpdateReservationRequest request);
}
