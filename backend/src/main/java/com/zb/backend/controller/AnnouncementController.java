package com.zb.backend.controller;

import com.zb.backend.annotation.CurrentAccount;
import com.zb.backend.constants.enums.AnnouncementEnum;
import com.zb.backend.constants.enums.EquipmentEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.entity.Announcement;
import com.zb.backend.entity.Equipment;
import com.zb.backend.model.JwtClaim;
import com.zb.backend.model.PageRequest;
import com.zb.backend.model.PageResult;
import com.zb.backend.model.Result;
import com.zb.backend.model.request.AddAnnouncementRequest;
import com.zb.backend.model.request.AddEquipmentRequest;
import com.zb.backend.service.AnnouncementService;
import com.zb.backend.service.EquipmentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ann")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    // 新增设备
    @Operation(summary = "新增公告 Admin")
    @PostMapping("/addAnnouncement")
    public Result<Boolean> addAnnouncement(
            @RequestBody AddAnnouncementRequest request,
            @CurrentAccount JwtClaim jwtClaim) {
        ResultEnum resultEnum = announcementService.addAnnouncement(request, jwtClaim);
        if (!resultEnum.getCode().equals(2001)) return Result.error(resultEnum, false);
        return Result.success(resultEnum, true);
    }

    // 分页获取
    @Operation(summary = "分页获取公告")
    @PostMapping("/getAnnouncement")
    public Result<PageResult<Announcement>> getAnnouncement(@RequestBody PageRequest request) {
        PageResult<Announcement> pageResult = announcementService.getAnnouncement(request);
        return Result.success(AnnouncementEnum.SUC_GET_ANN, pageResult);
    }

    // 修改公告
    @Operation(summary = "修改公告 Admin")
    @PostMapping("/updateAnnouncement")
    public Result<Boolean> updateAnnouncement(@RequestBody Announcement announcement) {
        ResultEnum resultEnum = announcementService.updateAnnouncement(announcement);
        if (!resultEnum.getCode().equals(2001)) return Result.error(resultEnum, false);
        return Result.success(resultEnum, true);
    }

    // 删除公告
    @Operation(summary = "删除公告 Admin")
    @PostMapping("/deleteAnnouncement")
    public Result<Boolean> deleteAnnouncement(@RequestBody Long announcementId) {
        ResultEnum resultEnum = announcementService.deleteAnnouncement(announcementId);
        if (!resultEnum.getCode().equals(2001)) return Result.error(resultEnum, false);
        return Result.success(resultEnum, true);
    }
}
