-- 主 data.sql 文件

-- 部门数据
RUNSCRIPT FROM 'classpath:/db/data/01_departments.sql';

-- 账号数据
RUNSCRIPT FROM 'classpath:/db/data/02_accounts.sql';

-- 员工数据
RUNSCRIPT FROM 'classpath:/db/data/03_employees.sql';

-- 会议室数据
RUNSCRIPT FROM 'classpath:/db/data/04_meeting_rooms.sql';

-- 设备数据
RUNSCRIPT FROM 'classpath:/db/data/05_equipment.sql';

-- 会议室可用状态
RUNSCRIPT FROM 'classpath:/db/data/room_availability.sql';

-- 会议室设备数据
RUNSCRIPT FROM 'classpath:/db/data/room_equipment.sql';

