package com.zb.backend.controller;

import com.zb.backend.constants.enums.DepartmentEnum;
import com.zb.backend.model.Result;
import com.zb.backend.model.response.SimpleDepartmentResponse;
import com.zb.backend.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dept")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @Operation(summary = "获取简单部门集合")
    @PostMapping("/getSimpleDepartmentList")
    public Result<List<SimpleDepartmentResponse>> getSimpleDepartmentList() {
        List<SimpleDepartmentResponse> simpleDeptList = departmentService.getSimpleDepartmentList();
        return Result.success(DepartmentEnum.SUC_SIMPLE_LIST, simpleDeptList);
    }
}
