DROP TABLE IF EXISTS room_equipment;
DROP TABLE IF EXISTS equipment;

-- 设备表，会议室用
CREATE TABLE equipment
(
    equipment_id     BIGINT DEFAULT NEXTVAL('equipment_seq') COMMENT '主键，设备类型 ID',
    equipment_name   VARCHAR(127) UNIQUE NOT NULL COMMENT '设备名称',
    description      VARCHAR(255) COMMENT '设备描述',
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    -- 设置主键
    PRIMARY KEY (equipment_id)
);
