package com.zb.backend.mapper;

import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.entity.Notification;
import com.zb.backend.model.PageRequest;

import java.util.List;

public interface NotificationMapper {
    // 添加通知
    Boolean insertNotification(Long senderId, Long receiverId, String notificationType, String title, String content, Long relatedId);

    // 获取总记录数
    Integer countNotifyList(Long accountId);

    // 获取通知集合
    List<Notification> selectNotifyList(PageRequest pageRequest, Long accountId);

    // 根据id获取未读数量
    Integer countNotRedNum(Long accountId);

    // 根据接收人id全部已读
    Integer updateAllReadByAccId(Long accountId);
}
