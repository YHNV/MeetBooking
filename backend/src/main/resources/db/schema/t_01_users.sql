DROP TABLE IF EXISTS users;

-- Test用户表，只包含用户名和密码，不加密
CREATE TABLE users
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '工号（登录账号）',
    password VARCHAR(50) NOT NULL COMMENT '密码（暂不加密）'
);