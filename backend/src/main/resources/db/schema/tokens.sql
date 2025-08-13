-- 如果表已存在，先删除（避免冲突）
DROP TABLE IF EXISTS tokens;

-- 创建 Token 黑名单表
CREATE TABLE tokens
(
    id          INT AUTO_INCREMENT PRIMARY KEY, -- 自增主键
    token       VARCHAR(255) NOT NULL,          -- JWT Token（带或不带 Bearer）
    account_id     BIGINT  NOT NULL,          -- 用户 ID（UUID 或数字）
    expiry_time TIMESTAMP    NOT NULL,          -- 过期时间（当前时间 + 4 小时）
    UNIQUE (token)                              -- 确保 Token 唯一
);

COMMENT ON TABLE tokens IS 'Token存储表';