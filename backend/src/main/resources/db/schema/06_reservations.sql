DROP TABLE IF EXISTS reservation_attendees;
DROP TABLE IF EXISTS reservations;

-- 预约表
CREATE TABLE reservations
(
    reservation_id     BIGINT DEFAULT NEXTVAL('reservation_seq') COMMENT '预约ID，主键',
    room_id            BIGINT       NOT NULL COMMENT '关联的会议室ID',
    account_id            BIGINT       NOT NULL COMMENT '申请人ID',
    emp_name          VARCHAR(63)  NOT NULL COMMENT '申请人姓名',
    meeting_topic  VARCHAR(127) NOT NULL COMMENT '预约标题/会议主题',
    meeting_desc    Text COMMENT '会议详情',
    reservation_date               DATE     NOT NULL COMMENT '预约日期',
    start_time         TIME    NOT NULL COMMENT '开始时间',
    end_time           TIME    NOT NULL COMMENT '结束时间',
    reservation_status ENUM (
        'PENDING', -- 待审核
        'APPROVED', -- 已通过
        'REJECTED', -- 已拒绝
        'CANCELLED', -- 已取消
        'EXPIRED' -- 已过期
        ) DEFAULT 'PENDING' COMMENT '预约状态：待审核/已通过/已拒绝/已取消/已过期',
    approval_account_id   BIGINT COMMENT '审核人ID',
    approval_time      TIMESTAMP COMMENT '审核时间',
    reject_reason    TEXT COMMENT '拒绝原因',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    -- 设置主键
    PRIMARY KEY (reservation_id),
    -- 外键约束
    FOREIGN KEY (room_id) REFERENCES MEETING_ROOMS(room_id) ON DELETE RESTRICT, -- 会议室被删除时限制操作
    FOREIGN KEY (account_id) REFERENCES ACCOUNTS(account_id),
    -- 检查时间是否合法
    -- 核心：时间合法性约束（适配每半小时一个区间的规则）
    CHECK (
        -- 1. 时间范围：开始时间≥8:00，结束时间≤20:00（你的业务时间范围）
        start_time >= '09:00:00' AND end_time <= '17:00:00'
            -- 2. 开始时间 < 结束时间
            AND start_time < end_time
            -- 3. 每半小时一个区间：分钟必须是00或30（核心规则）
            AND MINUTE(start_time) IN (0, 30)
            AND MINUTE(end_time) IN (0, 30)
            -- 4. 确保结束时间不早于9:00（如果需要，可保留原逻辑）
            AND start_time <= '16:30:00'
            AND end_time >= '09:30:00'
        )
);

COMMENT ON TABLE reservations IS '会议室预约表';