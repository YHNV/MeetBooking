package com.zb.backend.model.request;

import com.zb.backend.entity.enums.RoomStatus;
import com.zb.backend.entity.enums.RoomType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class UpdateMeetingRoomRequest {
    @NotNull(message = "会议室id不能为空")
    private Long roomId;

    @NotBlank(message = "会议室名称不能为空")
    @Size(max = 31, message = "会议室名称长度不能超过31个字符")
    private String roomName;

    @NotBlank(message = "会议室类型不能为空")
    @Pattern(regexp = "^(SMALL|LARGE)$", message = "会议室类型必须是SMALL或LARGE")
    private String roomType;  // 改为枚举类型

    @NotNull(message = "会议室容量不能为空")
    @Min(value = 1, message = "会议室容量不能小于1")
    private Integer capacity;

    @NotBlank(message = "会议室地址不能为空")
    @Size(max = 255, message = "会议室地址长度不能超过255个字符")
    private String location;

    @NotBlank(message = "会议室状态不能为空")
    @Pattern(regexp = "^(AVAILABLE|DISABLED|MAINTENANCE)$", message = "会议室状态必须是AVAILABLE、DISABLED或MAINTENANCE")
    private String roomStatus;  // 默认值

    // 图片路径（可为null，前端未上传时不传或传null）
    private String imageUrl;

    // 设备ID列表（可为空列表，表示不关联任何设备）
    private List<Long> equipmentIds;
}
