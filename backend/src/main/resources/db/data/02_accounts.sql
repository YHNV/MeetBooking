DELETE
FROM accounts;

-- 插入管理员账号信息数据
INSERT INTO accounts (account_id, password, is_admin, first_login)
VALUES (NEXTVAL('admin_seq'), '$2a$10$p6Qnyo.WA5zfXYuUedpl1uTT/BHGGd4yRZPOkG.Ver7cRkIBjxEza', TRUE, FALSE),
       (NEXTVAL('admin_seq'), '$2a$10$p6Qnyo.WA5zfXYuUedpl1uTT/BHGGd4yRZPOkG.Ver7cRkIBjxEza', TRUE, FALSE),
       (NEXTVAL('admin_seq'), '$2a$10$p6Qnyo.WA5zfXYuUedpl1uTT/BHGGd4yRZPOkG.Ver7cRkIBjxEza', TRUE, FALSE);

-- 插入员工账号信息数据
INSERT INTO accounts (password, IS_ACTIVE)
VALUES ('$2a$10$p6Qnyo.WA5zfXYuUedpl1uTT/BHGGd4yRZPOkG.Ver7cRkIBjxEza', true),
       ('$2a$10$p6Qnyo.WA5zfXYuUedpl1uTT/BHGGd4yRZPOkG.Ver7cRkIBjxEza', false),
       ('$2a$10$p6Qnyo.WA5zfXYuUedpl1uTT/BHGGd4yRZPOkG.Ver7cRkIBjxEza', true);

