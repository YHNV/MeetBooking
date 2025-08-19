-- 主 schema.sql 文件

-- 清空数据库
Drop ALL OBJECTS;

-- Token表
RUNSCRIPT FROM 'classpath:/db/schema/tokens.sql';

-- ID 序列
RUNSCRIPT FROM 'classpath:/db/schema/00_sequences.sql';

-- 部门表
RUNSCRIPT FROM 'classpath:/db/schema/01_departments.sql';

-- 账号表
RUNSCRIPT FROM 'classpath:/db/schema/02_accounts.sql';

-- 员工表
RUNSCRIPT FROM 'classpath:/db/schema/03_employees.sql';

-- 会议室表
RUNSCRIPT FROM 'classpath:/db/schema/04_meeting_rooms.sql';

-- 设备表
RUNSCRIPT FROM 'classpath:/db/schema/05_equipment.sql';

-- 会议室预约状态表
RUNSCRIPT FROM 'classpath:/db/schema/room_availability.sql';

-- 会议室设备关联表
RUNSCRIPT FROM 'classpath:/db/schema/room_equipment.sql';

-- 预约表
RUNSCRIPT FROM 'classpath:/db/schema/06_reservations.sql';

-- 会议人员参与表
RUNSCRIPT FROM 'classpath:/db/schema/reservation_attendees.sql';

-- 通知表
RUNSCRIPT FROM 'classpath:/db/schema/07_notifications.sql';

-- 公告表
RUNSCRIPT FROM 'classpath:/db/schema/08_announcements.sql';

