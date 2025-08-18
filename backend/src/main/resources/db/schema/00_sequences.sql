DROP SEQUENCE IF EXISTS dept_seq;
DROP SEQUENCE IF EXISTS account_seq;
DROP SEQUENCE IF EXISTS emp_seq;
DROP SEQUENCE IF EXISTS admin_seq;

DROP SEQUENCE IF EXISTS room_seq;
DROP SEQUENCE IF EXISTS equipment_seq;
DROP SEQUENCE IF EXISTS reservation_seq;



-- 部门 ID 序列
CREATE SEQUENCE dept_seq START WITH 10001;

-- 账号 ID 序列
CREATE SEQUENCE account_seq START WITH 10001;

-- 用户 ID 序列
CREATE SEQUENCE emp_seq START WITH 10001; -- 员工工号/员工账号序列，从10001开始
CREATE SEQUENCE admin_seq START WITH 101; -- 管理员账号序列，从101开始

-- 会议室 ID 序列
CREATE SEQUENCE room_seq START WITH 10001;

-- 设备类型 ID 序列
CREATE SEQUENCE equipment_seq START WITH 10001;

-- 预约 ID 序列
CREATE SEQUENCE reservation_seq START WITH 10001;

-- 通知 ID 序列


-- 公告 ID序列
