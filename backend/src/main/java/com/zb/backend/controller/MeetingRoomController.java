package com.zb.backend.controller;

import com.zb.backend.constants.enums.AccountEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.constants.enums.UploadEnum;
import com.zb.backend.model.Result;
import com.zb.backend.model.request.AddMeetingRoomRequest;
import com.zb.backend.service.MeetingRoomService;
import com.zb.backend.service.upload.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/meetingRoom")
@RequiredArgsConstructor
public class MeetingRoomController {
    private final MeetingRoomService meetingRoomService;
    private final FileUploadService fileUploadService;

    /*
    *
    * 我现在知道了，刚刚仔细想了想，我重新设计了一下我的创建会议室接口：
    首先在前端有三段结构：
    上传图片
    会议室信息
    设备管理

    其中，上传图片旁边有个按钮，点击上传，或者上传图片完成自动上传图片，后端返回来一个路径“/resources/***.jpg”，前端保存下这个路径
    中间就填写会议室信息
    设备管理这一块，首先在点击新增会议室的时候，前端就调用一个查询所有设备的接口，返回所有的设备信息，然后前端前端通过列表展示出来，点击每一项，将每一项equipment_id存下来，放入集合中，该集合equipment_id不能重复

    当点击确认创建的时候，前端自己校验一下，是否有上传图片，是否得到图片路径，如果没有上传，就为空就好了，因为后端图片路径可以为空，前端解析为空设置默认就好了，所以可以为空
    然后传给后端json数据，后端获取到这些数据后，要同时操作两张表，会议室表和会议室设备关联表，所以肯定是要加上事务的

    后端Service返回ResultEnum，首先判断名字，是不是存在了，然后执行创建会议室的SQL，图片路径也说了，没上传就为null就可以了

    当上传完成后，通过一个方法，得到刚刚创建的会议室的ID（能通过SQL返回吗？不能的话没关系，直接通过name查也行，反正name是唯一的）

    然后执行创建会议室设备关联表的SQL，通过会议室id和传过来的设备id，这两个一组，创建关联关系，再创建之前，先查询当前会议室id是否有存在关联，如果有，把所有设备关联删除，当然，这些删除操作都建立在事务的关系上，一旦一个地方出错，全部回滚
    *
    *  */

    @Operation(summary = "上传会议室图片 Admin")
    @PostMapping("/uploadImage")
    public Result<String> uploadImage(@RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        String imageUrl = fileUploadService.imageUpload(imageFile);
        return Result.success(UploadEnum.SUC_IMAGE_UPLOAD, imageUrl);
    }

    @Operation(summary = "新增会议室（带图片和设备） Admin")
    @PostMapping("/addMeetingRoom")
    public Result<Boolean> addMeetingRoom(@Valid @RequestBody AddMeetingRoomRequest addMeetingRoomRequest) {
        System.out.println(this.getClass().getSimpleName() + " 新增会议室：" + addMeetingRoomRequest);
        ResultEnum resultEnum = meetingRoomService.addMeetingRoom(addMeetingRoomRequest);
        if (!resultEnum.getCode().equals(2001)) {
            return Result.error(resultEnum, false);
        }
        return Result.success(resultEnum, true);
    }


}
