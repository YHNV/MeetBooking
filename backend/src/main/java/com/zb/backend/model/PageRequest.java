package com.zb.backend.model;

import lombok.Data;

@Data
public class PageRequest {
    // 分页参数
    private Integer pageNum = 1; // 默认第一页
    private Integer pageSize = 10; // 默认查询10条
}
