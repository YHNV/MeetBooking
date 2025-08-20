package com.zb.backend.controller;

import com.zb.backend.model.PageResult;
import com.zb.backend.model.Result;
import com.zb.backend.model.request.QueryEmployeesRequest;
import com.zb.backend.model.request.UpdateEmployeeInfo;
import com.zb.backend.model.response.QueryEmployeesResponse;
import com.zb.backend.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emp")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    // 分页查询员工信息
    @Operation(summary = "分页查询员工信息 Admin")
    @PostMapping("/queryEmployees")
    public Result<PageResult<QueryEmployeesResponse>> queryEmployees(@RequestBody QueryEmployeesRequest queryRequest) {
        System.out.println(this.getClass().getSimpleName() + "：" + queryRequest);

        PageResult<QueryEmployeesResponse> pageResult = employeeService.queryEmployees(queryRequest);

        return Result.success(2001, "查询成功", pageResult);
    }

    // 修改员工信息
    @Operation(summary = "修改员工信息 Admin")
    @PostMapping("/updateEmployeeInfo")
    public Result<Boolean> updateEmployeeInfo(@Valid @RequestBody UpdateEmployeeInfo updateEmployeeInfo) {

        Boolean update = employeeService.updateEmployeeInfo(updateEmployeeInfo);

        if (!update) {
            return Result.success(4001, "修改失败", false);
        }

        return Result.success(2001, "修改成功", true);
    }
}
