DROP TABLE IF EXISTS notifications;

CREATE TABLE notifications (
    notification_id BIGINT DEFAULT NEXTVAL('notification_seq') COMMENT '通知ID，主键',
    sender_id   BIGINT NOT NULL COMMENT '发送人ID，非空，关联管理员ID',
    receiver_id BIGINT NOT NULL COMMENT '收件人ID，非空，关联员工ID',
    notification_type ENUM(
        'MENTION', -- 提醒通知
        'SYSTEM', -- 系统通知
        'ADMIN', -- 管理员通知
        'APPROVAL', -- 审核通过通知
        'REJECTION' -- 审核拒绝通知
        ) DEFAULT 'SYSTEM' COMMENT '通知类型',
    title VARCHAR(127) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    related_id BIGINT COMMENT '关联业务ID，关联预约ID',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读（默认未读）',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    read_time TIMESTAMP COMMENT '阅读时间（已读时更新）',
    -- 添加主键
    PRIMARY KEY (notification_id),
    -- 设置外键约束
    FOREIGN KEY (sender_id) REFERENCES ACCOUNTS(account_id),
    FOREIGN KEY (receiver_id) REFERENCES ACCOUNTS(account_id)
    -- FOREIGN KEY (related_id) REFERENCES RESERVATIONS(reservation_id)
);

COMMENT ON TABLE notifications IS '通知表';

-- 索引：
-- 优化按接收人+未读状态查询
CREATE INDEX idx_notifications_receiver_status ON notifications(receiver_id, is_read);
CREATE INDEX idx_notifications_receiver ON notifications(receiver_id);
CREATE INDEX idx_notifications_read ON notifications(is_read);
CREATE INDEX idx_notifications_create ON notifications(create_time);