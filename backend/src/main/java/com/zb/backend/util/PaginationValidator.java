package com.zb.backend.util;

import com.zb.backend.model.PageRequest;
import com.zb.backend.model.PageResult;

import java.util.List;

public class PaginationValidator {
    /**
     * 校验并修正分页参数（pageNum和pageSize）
     * 当pageNum为空或小于1时，默认为1
     * 当pageSize为空、小于1或大于50时，设置为10
     *
     * @param queryRequest 分页请求对象
     */
    public static void validatePageParameters(PageRequest queryRequest) {
        // 校验并修正页码
        if (queryRequest.getPageNum() == null || queryRequest.getPageNum() < 1) {
            queryRequest.setPageNum(1);
        }

        // 校验并修正每页条数
        if (queryRequest.getPageSize() == null || queryRequest.getPageSize() < 1 || queryRequest.getPageSize() > 50) {
            queryRequest.setPageSize(10);
        }
    }

    /**
     * 检查并修正页码，确保不超过总页数
     * 如果当前页大于总页数，将其设置为第一页
     *
     * @param queryRequest 分页请求对象
     * @param total 总记录数
     */
    public static void adjustPageNumIfNecessary(PageRequest queryRequest, long total) {
        // 计算总页数
        int totalPages = total > 0 ? (int) Math.ceil((double) total / queryRequest.getPageSize()) : 1;

        // 如果当前页超过总页数，重置为第一页
        if (queryRequest.getPageNum() > totalPages) {
            queryRequest.setPageNum(1);
        }
    }

    /**
     * 检查总记录数是否为0，如果是则返回空结果集
     *
     * @param total 总记录数
     * @param queryRequest 分页请求对象
     * @return 如果总记录数为0，返回空结果集的PageResult，否则返回null
     */
    public static <T> PageResult<T> checkForEmptyResult(long total, PageRequest queryRequest) {
        if (total == 0) {
            return new PageResult<>(0, queryRequest.getPageNum(), queryRequest.getPageSize(), List.of());
        }
        return null;
    }

    /**
     * 执行完整的分页参数校验流程
     * 包括参数修正、页码调整和空结果检查
     *
     * @param queryRequest 分页请求对象
     * @param total 总记录数
     * @return 如果总记录数为0，返回空结果集的PageResult，否则返回null
     */
    public static <T> PageResult<T> validatePagination(PageRequest queryRequest, long total) {
        // 校验基本分页参数
        validatePageParameters(queryRequest);

        // 检查是否有记录
        PageResult<T> emptyResult = checkForEmptyResult(total, queryRequest);
        if (emptyResult != null) {
            return emptyResult;
        }

        // 调整页码（如果需要）
        adjustPageNumIfNecessary(queryRequest, total);

        return null;
    }
}
