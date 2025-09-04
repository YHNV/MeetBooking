package com.zb.backend.controller;

import com.zb.backend.constants.enums.DepartmentEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.model.Result;
import com.zb.backend.model.request.DeptRequest;
import com.zb.backend.model.response.GetAllDepartmentResponse;
import com.zb.backend.model.response.SimpleDepartmentResponse;
import com.zb.backend.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Operation(summary = "获取全部部门信息 Admin")
    @PostMapping("/getAllDeptList")
    public Result<List<GetAllDepartmentResponse>> getAllDeptList() {
        List<GetAllDepartmentResponse> allDeptList = departmentService.getAllDept();
        return Result.success(DepartmentEnum.SUC_GET_ALL, allDeptList);
    }

    @Operation(summary = "新增部门 Admin")
    @PostMapping("/addDept")
    public Result<Boolean> addDept(@Valid @RequestBody DeptRequest request) {
        ResultEnum resultEnum = departmentService.addDept(request);
        if (!resultEnum.getCode().equals(2001)) return Result.error(resultEnum, false);
        return Result.success(resultEnum, true);
    }

    /*
    *
    * 修改部门逻辑
    * 首先查看部门id是否存在
    * 判断名称和部门经理id是否和原来的相同
    * 如果不相同，把原来的部门经理，只修改职位和isManager
    * 有新的经理，套用新增d逻辑
    *
    *  */

    @Operation(summary = "修改部门 Admin")
    @PostMapping("/updateDept")
    public Result<Boolean> updateDept(@Valid @RequestBody DeptRequest request) {
        ResultEnum resultEnum = departmentService.updateDept(request);
        if (!resultEnum.getCode().equals(2001)) return Result.error(resultEnum, false);
        return Result.success(resultEnum, true);
    }

}
