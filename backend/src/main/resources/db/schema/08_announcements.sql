DROP TABLE IF EXISTS announcements;

CREATE TABLE announcements
(
    announcement_id BIGINT DEFAULT NEXTVAL('announcement_seq') COMMENT '公告ID，主键',
    sender_id       BIGINT NOT NULL COMMENT '发送人ID，非空，关联管理员ID',
    title VARCHAR(127) NOT NULL COMMENT '公告标题',
    content TEXT NOT NULL COMMENT '公告内容',
    is_active BOOLEAN DEFAULT TRUE COMMENT '公告是否生效',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    -- 设置主键
    PRIMARY KEY (announcement_id),
    -- 设置约束
    FOREIGN KEY (sender_id) REFERENCES ACCOUNTS(account_id)
);

-- 创建索引
CREATE INDEX idx_announcement_active ON announcements(is_active);








