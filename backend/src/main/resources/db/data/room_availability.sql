-- 先清空表中已有数据（可选）
DELETE
FROM room_availability;

-- 插入15天内（含今天）的可用状态数据（排除周六日）
INSERT INTO room_availability (room_id, schedule_date)
WITH RECURSIVE date_series(date, day_count) AS (
    -- 起始行：明天，计数1
    SELECT CURRENT_DATE + 1 AS date, 1 AS day_count
    UNION ALL
    -- 递归生成后续日期，直到15天
    SELECT DATEADD('DAY', 1, date), day_count + 1
    FROM date_series
    WHERE day_count < 15)
-- 关联会议室表，生成所有会议室的15天数据（仅保留工作日）
SELECT mr.room_id,
       ds.date AS schedule_date
FROM meeting_rooms mr
         CROSS JOIN date_series ds
-- 过滤条件：仅保留周一至周五（H2中DAY_OF_WEEK返回1=周日，2=周一，...，6=周五，7=周六）
WHERE DAY_OF_WEEK(ds.date) BETWEEN 2 AND 6;
