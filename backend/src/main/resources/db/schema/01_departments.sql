DROP TABLE IF EXISTS departments;

-- 创建部门表
CREATE TABLE departments
(
    dept_id      BIGINT    DEFAULT NEXTVAL('dept_seq') COMMENT '部门ID，主键',
    dept_name    VARCHAR(63) NOT NULL UNIQUE COMMENT '部门名称，非空，唯一',
    manager_id   BIGINT COMMENT '部门经理ID，关联员工表emp_id',
    dept_address VARCHAR(255) COMMENT '部门地址',
    dept_desc    TEXT COMMENT '部门详情',
    is_active    BOOLEAN   DEFAULT TRUE COMMENT '部门是否启用，默认true',
    create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (dept_id)
    -- 外键约束，当经理ID删除时，设置部门经理ID为NULL
    -- FOREIGN KEY (manager_id) REFERENCES employees (emp_id) ON DELETE SET NULL
);

COMMENT ON TABLE departments IS '部门信息表';

-- 确保employees表已经创建后，再添加外键约束（报错了，创建员工表要先创建部门，再加这个约束就冲突了）
-- ALTER TABLE departments
--     -- ADD CONSTRAINT fk_dept_manager
--     ADD FOREIGN KEY (manager_id) REFERENCES employees (emp_id) ON DELETE SET NULL;