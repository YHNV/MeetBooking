package com.zb.backend.controller;

import com.zb.backend.constants.enums.AccountEnum;
import com.zb.backend.model.Result;
import com.zb.backend.model.request.AddMeetingRoomRequest;
import com.zb.backend.service.MeetingRoomService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meetingRoom")
@RequiredArgsConstructor
public class MeetingRoomController {
    private final MeetingRoomService meetingRoomService;

    @Operation(summary = "新增会议室（不带图片） Admin")
    @PostMapping("/addMeetingRoom")
    public Result<Boolean> addMeetingRoom(AddMeetingRoomRequest addMeetingRoomRequest) {

        return Result.success(AccountEnum.SUC_DETAIL, true);
    }
}
