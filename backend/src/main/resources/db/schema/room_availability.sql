-- 删除旧表（如果存在）
DROP TABLE IF EXISTS room_availability;

-- 创建会议室可用状态表（每半小时一个区间）
CREATE TABLE room_availability
(
    room_id       BIGINT NOT NULL COMMENT '会议室ID',
    schedule_date DATE   NOT NULL COMMENT '日期',
    -- 9:00-17:00共8小时，每半小时一个区间，共16个区间
    -- 用16位整数表示状态：每位对应一个区间（0=可用，1=不可用）
    -- 二进制位从右到左对应区间：0=9:00-9:30，1=9:30-10:00，...，15=16:30-17:00
    slot_status   INT    NOT NULL DEFAULT 0 COMMENT '16个区间的状态（默认全可用）',
--     create_time   TIMESTAMP       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--     update_time   TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (room_id, schedule_date),                                       -- 复合主键：确保一个会议室一天一条记录
    FOREIGN KEY (room_id) REFERENCES meeting_rooms (room_id) ON DELETE CASCADE, -- 关联会议室表
    CHECK (slot_status >= 0 AND slot_status <= 65535),                          -- 16位整数范围约束（2^16-1=65535）
    CHECK (DAY_OF_WEEK(schedule_date) BETWEEN 2 AND 6)                          -- 仅允许周一至周五
);

COMMENT ON TABLE room_availability IS '会议室可用状态表（每半小时一个区间）';

-- 索引：优化按日期查询
CREATE INDEX IF NOT EXISTS idx_schedule_date ON room_availability (schedule_date);
