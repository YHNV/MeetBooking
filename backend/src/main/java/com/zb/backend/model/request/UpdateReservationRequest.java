package com.zb.backend.model.request;

import com.zb.backend.model.PageRequest;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReservationRequest {
    // 修改
    @NotNull(message = "预约id不能为空")
    private Long reservationId;
    // 预约状态
    @NotBlank(message = "预约状态不能为空")
    @Pattern(regexp = "^(APPROVED|REJECTED|CANCELLED)$", message = "会议室状态必须是APPROVED、REJECTED、CANCELLED")
    private String reservationStatus;
    // 拒绝原因，可以为空
    private String rejectReason;
    // 审核员id，不填，为空
    private Long approvalAccountId;
}




// 开始时间索引：0
// 结束时间索引：5
// 取消预约原状态：63-取反后：63
// 旧的状态值：63 按位与得到的新的状态值63