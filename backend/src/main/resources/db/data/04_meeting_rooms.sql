DELETE FROM MEETING_ROOMS;

INSERT INTO meeting_rooms (room_name, room_type, capacity, location, room_status, image_url)
VALUES
    ('小型会议室1', 'SMALL', 8, 'A栋3层301', 'AVAILABLE', NULL),
    ('小型会议室2', 'SMALL', 10, 'A栋3层302', 'AVAILABLE', NULL),
    ('小型会议室3', 'SMALL', 6, 'A栋3层303', 'AVAILABLE', NULL),
    ('小型会议室4', 'SMALL', 12, 'A栋4层401', 'AVAILABLE', NULL),
    ('小型会议室5', 'SMALL', 8, 'A栋4层402', 'MAINTENANCE', NULL),
    ('小型会议室6', 'SMALL', 10, 'A栋4层403', 'AVAILABLE', NULL),
    ('小型会议室7', 'SMALL', 15, 'C栋2层201', 'AVAILABLE', NULL),
    ('大型会议室1', 'LARGE', 25, 'B栋2层201', 'AVAILABLE', NULL),
    ('大型会议室2', 'LARGE', 30, 'B栋2层202', 'AVAILABLE', NULL),
    ('大型会议室3', 'LARGE', 35, 'B栋3层301', 'DISABLED', NULL),
    ('大型会议室4', 'LARGE', 40, 'B栋3层302', 'AVAILABLE', NULL),
    ('大型会议室5', 'LARGE', 28, 'B栋3层303', 'AVAILABLE', NULL),
    ('大型会议室6', 'LARGE', 45, 'B栋4层401', 'AVAILABLE', NULL);