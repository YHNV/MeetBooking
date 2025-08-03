DROP TABLE IF EXISTS accounts;

-- 创建账号表
-- 账号ID为员工工号，与员工表ID相关联
CREATE TABLE accounts
(
    account_id      BIGINT    DEFAULT NEXTVAL('account_seq') COMMENT '账号ID，主键',
    password        VARCHAR(63) NOT NULL COMMENT '加密后的密码，非空',
    is_admin        BOOLEAN   DEFAULT FALSE COMMENT '是否为管理员，默认false',
    is_active       BOOLEAN   DEFAULT TRUE COMMENT '账号是否启用，默认true',
    first_login     BOOLEAN   DEFAULT TRUE COMMENT '是否首次登录，默认true',
    last_login_time TIMESTAMP COMMENT '最后登录时间',
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    -- 设置主键
    PRIMARY KEY (account_id)
);

COMMENT ON TABLE accounts IS '账号信息表';

-- 创建触发器（报错，用不了）
-- CREATE TRIGGER IF NOT EXISTS assign_account_id
--     BEFORE INSERT ON accounts
--     FOR EACH ROW
--     CALL "org.h2.api.TriggerAdapter"
--     $$
--         -- 如果是管理员账号 is_admin=true 使用admin_seq
--         IF NEW.is_admin = TRUE THEN
--             SET NEW.account_id = NEXTVAL('admin_seq');
--         -- 否则则使用员工序列 emp_seq
--         ELSE
--             SET NEW.account_id = NEXTVAL('emp_seq');
--         END IF;
--     $$