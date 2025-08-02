-- 主 data.sql 文件

-- 部门数据
RUNSCRIPT FROM 'classpath:/db/data/01_departments.sql';

-- 账号数据
RUNSCRIPT FROM 'classpath:/db/data/02_accounts.sql';

-- 员工数据
RUNSCRIPT FROM 'classpath:/db/data/03_employees.sql';

-- Test用户数据
RUNSCRIPT FROM 'classpath:/db/data/t_01_users.sql';