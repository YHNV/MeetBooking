package com.zb.backend.model.request;

import com.zb.backend.entity.enums.ReservationStatus;
import com.zb.backend.entity.enums.RoomStatus;
import com.zb.backend.model.PageRequest;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class QueryReservationRequest extends PageRequest {
    // 查询条件
    // accountId，只为空，不传值
    private Long accountId;
    // 日期
    @FutureOrPresent(message = "预约日期不能为过去的日期")
    private LocalDate reservationDate;
    // 预约状态
    @Pattern(regexp = "^(PENDING|APPROVED|REJECTED|CANCELLED|EXPIRED)?$", message = "会议室状态必须是PENDING、APPROVED、REJECTED、CANCELLED、EXPIRED或为空")
    private String reservationStatus;
}
