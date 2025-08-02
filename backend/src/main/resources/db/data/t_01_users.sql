DELETE FROM users;

-- 初始密码为"yh@6位数生日"（符合前端提示）
INSERT INTO users (username, password)
VALUES ('1001', 'yh@010101');
INSERT INTO users (username, password)
VALUES ('1002', 'yh@020202');