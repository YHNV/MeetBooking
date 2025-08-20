package com.zb.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
    // 总记录数
    private Integer total;
    // 总页数
    private Integer pages;
    // 当前页码
    private Integer pageNum;
    // 每页条数
    private Integer pageSize;
    // 当前页数据集合
    private List<T> list;

    // 快捷构造方法（根据分页参数和数据集合计算总页数）
    public PageResult(Integer total, Integer pageNum, Integer pageSize, List<T> list) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.list = list;
        // 计算总页数（向上取整）
        this.pages = (int) Math.ceil((double) total / pageSize);
        if (pageNum >= pages) {
            this.pageNum = 1;
        }
    }
}
