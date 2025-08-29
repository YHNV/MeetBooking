package com.zb.backend.model.request;

import com.zb.backend.entity.enums.ReservationStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequest {
    /**
     * 关联的会议室ID
     * 非空约束，对应数据库NOT NULL
     */
    @NotNull(message = "会议室ID不能为空")
    private Long roomId;

    /**
     * 预约标题/会议主题
     * 非空且长度约束，对应数据库VARCHAR(127) NOT NULL
     */
    @NotNull(message = "会议主题不能为空")
    @Size(max = 127, message = "会议主题长度不能超过127个字符")
    private String meetingTopic;

    /**
     * 会议详情
     * 长度建议约束，对应数据库Text类型
     */
    @Size(max = 2000, message = "会议描述不能超过2000个字符")
    private String meetingDesc;

    /**
     * 预约日期
     * 非空且不能为过去的日期（根据业务需求调整）
     */
    @NotNull(message = "预约日期不能为空")
    @FutureOrPresent(message = "预约日期不能为过去的日期")
    private LocalDate reservationDate;

    /**
     * 开始时间
     * 非空且符合业务时间规则（9:00-17:00，每半小时为间隔）
     */
    @NotNull(message = "开始时间不能为空")
    private LocalTime startTime;

    /**
     * 结束时间
     * 非空且符合业务时间规则，且必须晚于开始时间
     */
    @NotNull(message = "结束时间不能为空")
    private LocalTime endTime;

    /**
     * 参会人ID列表，可以为空，就是只有预约人自己
     */
    private List<Long> attendees;

    /**
     * 是否提醒所有人
     * 非空约束（建议）
     */
    @NotNull(message = "是否提醒所有人不能为空")
    private Boolean mentionAll;
}
