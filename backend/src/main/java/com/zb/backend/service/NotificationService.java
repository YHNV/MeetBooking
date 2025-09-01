package com.zb.backend.service;

import com.zb.backend.constants.enums.NotificationEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.entity.Notification;
import com.zb.backend.mapper.NotificationMapper;
import com.zb.backend.model.JwtClaim;
import com.zb.backend.model.PageRequest;
import com.zb.backend.model.PageResult;
import com.zb.backend.model.response.QueryEmployeesResponse;
import com.zb.backend.util.PaginationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationMapper notificationMapper;

    // 添加通知
    public Boolean addNotification(Long senderId, Long accountId, String notificationType, String title, String content, Long reservationId) {
        return notificationMapper.insertNotification(senderId, accountId, notificationType, title, content, reservationId);
    }

    // 获取通知
    public PageResult<Notification> getNotifications(PageRequest pageRequest, JwtClaim jwtClaim) {
        // 获取jwtClaim的值
        Long accountId = jwtClaim.getAccountId();
        Boolean isAdmin = jwtClaim.getIsAdmin();

        List<Notification> notificationList;
        Integer total = 0;

        // 根据isAdmin来查询总数  是管理员的情况下
        if (isAdmin) accountId = null;

        // 获取总数
        total = notificationMapper.countNotifyList(accountId);
        // 分页校验工具类
        PageResult<Notification> pageResult = PaginationValidator.validatePagination(pageRequest, total);
        if (pageResult != null) return pageResult;

        System.out.println("当前查询是否为管理员：" + isAdmin + " 查询总记录数为：" + total);
        // 记录数不为0，根据筛选条件查询
        notificationList = notificationMapper.selectNotifyList(pageRequest, accountId);
        return new PageResult<>(total, pageRequest.getPageNum(), pageRequest.getPageSize(), notificationList);
    }

    // 获取未读消息数量
    public Integer getNotReadNum(JwtClaim jwtClaim) {
        // 首先判断是否为员工
        if (jwtClaim.getIsAdmin()) throw new RuntimeException(NotificationEnum.ERR_NOT_EMP.getMessage());
        return notificationMapper.countNotRedNum(jwtClaim.getAccountId());

    }

    // 全部已读
    public ResultEnum readAllNotifications(JwtClaim jwtClaim) {
        // 首先判断是否为员工
        if (jwtClaim.getIsAdmin()) throw new RuntimeException(NotificationEnum.ERR_NOT_EMP.getMessage());
        Integer readNum = notificationMapper.updateAllReadByAccId(jwtClaim.getAccountId());
        System.out.println("已读通知：" + readNum + "条");
        return NotificationEnum.SUC_ALL_READ;
    }
}
