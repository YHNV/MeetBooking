-- 如果表已存在，先删除（避免冲突）
DROP TABLE IF EXISTS tokens;

-- 创建 Token表
CREATE TABLE tokens
(
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    token       VARCHAR(255) NOT NULL UNIQUE COMMENT 'Token字段，非空唯一',
    account_id  BIGINT       NOT NULL COMMENT 'Token所属的accountId',
    expiry_time TIMESTAMP    NOT NULL COMMENT 'Token过期时间，创建时间+2小时'
);

COMMENT ON TABLE tokens IS 'Token存储表';