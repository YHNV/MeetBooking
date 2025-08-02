DROP TABLE IF EXISTS employees;

-- 创建员工信息表
-- 员工ID和账号ID相关联，账号表为主表
-- 员工表dept_id关联部门表dept_id
CREATE TABLE employees
(
    emp_id      BIGINT    DEFAULT NEXTVAL('emp_seq') COMMENT '员工工号，主键',
    emp_name    VARCHAR(31) NOT NULL COMMENT '员工姓名，非空',
    dept_id     BIGINT      NOT NULL COMMENT '所属部门ID，关联部门表dept_id，非空',
    position    VARCHAR(31) COMMENT '部门职位，可空',
    phone       VARCHAR(15) NOT NULL COMMENT '联系电话，非空',
    email       VARCHAR(63) COMMENT '提醒邮箱，可空',
    id_card     VARCHAR(18) NOT NULL UNIQUE COMMENT '身份证号，非空唯一',
    is_manager  BOOLEAN   DEFAULT FALSE COMMENT '是否为经理，默认false',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    -- 设置主键
    PRIMARY KEY (emp_id),
    -- 外键约束
    -- 当部门内还有员工时，无法删除该部门
    FOREIGN KEY (dept_id) REFERENCES departments (dept_id) ON DELETE RESTRICT,
    -- 员工工号关联账号
    FOREIGN KEY (emp_id) REFERENCES accounts (account_id)

    -- ON DELETE SET NULL：保留从表记录，标记关联为空（适合 “允许关联为空” 的场景）。
    -- ON DELETE CASCADE：级联删除从表记录（适合 “主数据删除后，关联数据无意义” 的场景）。
    -- ON DELETE RESTRICT：禁止删除主表记录（适合 “必须先处理关联数据才能删除主数据” 的场景）。
);

COMMENT ON TABLE employees IS '员工信息表';