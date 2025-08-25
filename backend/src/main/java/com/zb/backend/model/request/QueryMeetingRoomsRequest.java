package com.zb.backend.model.request;

import com.zb.backend.model.PageRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class QueryMeetingRoomsRequest extends PageRequest {
    // 分页参数
    // private Integer pageNum = 1; // 默认第一页
    // private Integer pageSize = 10; // 默认查询10条
    // 查询条件，为空默认查询所有
    private String roomName; // 会议室名称
    @Pattern(regexp = "^(|SMALL|LARGE)$", message = "会议室类型必须是SMALL或LARGE，或者为空")
    private String roomType; // 会议室类型
    // @NotNull(message = "最小容量不能为空")
    // @Min(value = 1, message = "最小容量不能小于1")
    private Integer minCapacity; // 最小容量
    // @NotNull(message = "最大容量不能为空")
    // @Min(value = 1, message = "最小容量不能小于1")
    private Integer maxCapacity; // 最大容量
    private String location; // 会议室地址
    @Pattern(regexp = "^(|AVAILABLE|DISABLED|MAINTENANCE)$", message = "会议室状态必须是AVAILABLE、DISABLED或MAINTENANCE，或者为空")
    private String roomStatus; // 会议室状态
    private List<Long> equipmentIdList; // 设备id集合
}
