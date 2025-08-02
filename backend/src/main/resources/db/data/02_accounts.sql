DELETE
FROM accounts;

-- 插入管理员账号信息数据
INSERT INTO accounts (account_id, password, is_admin, first_login)
VALUES (NEXTVAL('admin_seq'), 123456, TRUE, FALSE),
       (NEXTVAL('admin_seq'), 123456, TRUE, FALSE),
       (NEXTVAL('admin_seq'), 123456, TRUE, FALSE);

-- 插入员工账号信息数据
INSERT INTO accounts (password)
VALUES (123456),
       (123456),
       (123456);

