package com.zb.backend.model.request;

import com.zb.backend.entity.enums.RoomStatus;
import com.zb.backend.entity.enums.RoomType;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class AddMeetingRoomRequest {
    /**
     * 会议室名称
     * 非空，唯一
     */
    @NotBlank(message = "会议室名称不能为空")
    @Size(max = 31, message = "会议室名称长度不能超过31个字符")
    private String roomName;

    /**
     * 会议室种类（直接使用枚举类型）
     */
    @NotNull(message = "会议室类型不能为空")
    private RoomType roomType;  // 改为枚举类型

    /**
     * 会议室容量
     * 非空
     */
    @NotNull(message = "会议室容量不能为空")
    @Min(value = 1, message = "会议室容量不能小于1")
    private Integer capacity;

    /**
     * 会议室地址
     * 非空
     */
    @NotBlank(message = "会议室地址不能为空")
    @Size(max = 255, message = "会议室地址长度不能超过255个字符")
    private String location;

    /**
     * 会议室状态（直接使用枚举类型）
     */
    private RoomStatus roomStatus = RoomStatus.AVAILABLE;  // 默认值

    // 图片路径（可为null，前端未上传时不传或传null）
    private String imageUrl;

    // 设备ID列表（可为空列表，表示不关联任何设备）
    private List<Long> equipmentIds;
}
