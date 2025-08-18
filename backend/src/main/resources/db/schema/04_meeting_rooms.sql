DROP TABLE IF EXISTS room_equipment;
DROP TABLE IF EXISTS equipment;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS room_availability;
DROP TABLE IF EXISTS meeting_rooms;

-- 会议室
CREATE TABLE meeting_rooms
(
    room_id     BIGINT                                        DEFAULT NEXTVAL('room_seq') COMMENT '会议室ID，主键',
    room_name   VARCHAR(31)             NOT NULL UNIQUE COMMENT '会议室名称',
    room_type   ENUM ('SMALL', 'LARGE') DEFAULT 'SMALL' NOT NULL COMMENT '会议室种类：小型、大型',
    capacity    INT                     NOT NULL COMMENT '会议室容量',
    location    VARCHAR(255)            NOT NULL COMMENT '会议室地址',
    room_status ENUM ('AVAILABLE', 'DISABLED', 'MAINTENANCE') DEFAULT 'AVAILABLE' COMMENT '会议室状态：可用、不可用、维护',
    image_url   VARCHAR(255) COMMENT '会议室展示图片URL',
    create_time TIMESTAMP                                     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP                                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    -- 设置主键
    PRIMARY KEY (room_id)
);

COMMENT ON TABLE meeting_rooms IS '会议室表';

-- 创建索引
-- 按会议室容量查询
CREATE INDEX IF NOT EXISTS idx_capacity ON meeting_rooms (capacity);
-- 按会议室状态查询（高频场景：查询可用会议室）
CREATE INDEX IF NOT EXISTS idx_room_status ON meeting_rooms (room_status);
-- 按会议室类型查询（高频场景：筛选小型/大型会议室）
CREATE INDEX IF NOT EXISTS idx_room_type ON meeting_rooms (room_type);
