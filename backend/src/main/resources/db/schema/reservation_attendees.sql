-- 如果表已存在，先删除（避免冲突）
DROP TABLE IF EXISTS reservation_attendees;

-- 创建 会议人员参与表
CREATE TABLE reservation_attendees
(
    id             INT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    reservation_id BIGINT      NOT NULL COMMENT '预约ID',
    account_id     BIGINT      NOT NULL COMMENT '参会人ID',
    emp_name       VARCHAR(63) NOT NULL COMMENT '参会人姓名',
    -- 添加约束
    FOREIGN KEY (reservation_id) REFERENCES RESERVATIONS(reservation_id),
    FOREIGN KEY (account_id) REFERENCES ACCOUNTS(account_id),
    UNIQUE (reservation_id, account_id) -- 避免重复添加
);

COMMENT ON TABLE reservation_attendees IS '会议人员参与表';