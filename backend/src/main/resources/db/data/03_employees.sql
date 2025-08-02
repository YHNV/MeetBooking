DELETE
FROM employees;

-- 插入员工数据（仅3条，与账号表的普通员工账号一一对应）
INSERT INTO employees (emp_name, dept_id, position, phone, email, id_card, is_manager)
VALUES
    -- 1. 人力部员工（对应账号ID:10001）
    ('张三', 10001, '人力资源经理', '13800138001', 'zhangsan@example.com', '110101199001011234', TRUE),

    -- 2. 业务部员工（对应账号ID:10002）
    ('李四', 10002, '业务专员', '13800138002', 'lisi@example.com', '310101199203045678', FALSE),

    -- 3. 技术部员工（对应账号ID:10003）
    ('王五', 10008, '开发工程师', '13800138003', 'wangwu@example.com', '510101199305067890', FALSE);
