-- 主 schema.sql 文件
-- ID 序列
RUNSCRIPT FROM 'classpath:/db/schema/00_sequences.sql';

-- 部门表
RUNSCRIPT FROM 'classpath:/db/schema/01_departments.sql';

-- 账号表
RUNSCRIPT FROM 'classpath:/db/schema/02_accounts.sql';

-- 员工表
RUNSCRIPT FROM 'classpath:/db/schema/03_employees.sql';

-- Test用户表
RUNSCRIPT FROM 'classpath:/db/schema/t_01_users.sql';