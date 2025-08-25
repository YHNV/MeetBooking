DELETE FROM MEETING_ROOMS;

INSERT INTO MEETING_ROOMS (room_name, room_type, capacity, location, room_status, image_url)
VALUES
    ('小型会议室1', 'SMALL', 8, 'A栋3层301', 'AVAILABLE', '/resources/b6c0b9e0-83cd-4418-a838-74eb7a3e5c48.jpg'),
    ('小型会议室2', 'SMALL', 10, 'A栋3层302', 'AVAILABLE', '/resources/9a2f1d8c-5e3b-4a7d-b2c6-8f3e9a1b4c7d.png'),
    ('小型会议室3', 'SMALL', 6, 'A栋3层303', 'AVAILABLE', '/resources/c4e7f9a2-8b3d-4c6a-9e1f-3a5b8d2c7e6f.jpg'),
    ('小型会议室4', 'SMALL', 12, 'A栋4层401', 'AVAILABLE', '/resources/d8a3b9c7-5e2f-4a1d-9b6c-7f4e8a2b5c3d.jpeg'),
    ('小型会议室5', 'SMALL', 8, 'A栋4层402', 'MAINTENANCE', '/resources/e7f9a8b6-4d3c-5a2b-8c1d-9e6f7a4b5c3d.png'),
    ('小型会议室6', 'SMALL', 10, 'A栋4层403', 'AVAILABLE', '/resources/f8a9b7c6-5d4e-3a2b-9c1d-8e7f6a5b4c3d.jpg'),
    ('小型会议室7', 'SMALL', 15, 'C栋2层201', 'AVAILABLE', '/resources/1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d.jpeg'),
    ('大型会议室1', 'LARGE', 25, 'B栋2层201', 'AVAILABLE', '/resources/2b3c4d5e-6f7a-8b9c-0d1e-2f3a4b5c6d7e.png'),
    ('大型会议室2', 'LARGE', 30, 'B栋2层202', 'AVAILABLE', '/resources/3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f.jpg'),
    ('大型会议室3', 'LARGE', 35, 'B栋3层301', 'DISABLED', '/resources/4d5e6f7a-8b9c-0d1e-2f3a-4b5c6d7e8f9a.jpeg'),
    ('大型会议室4', 'LARGE', 40, 'B栋3层302', 'AVAILABLE', '/resources/5e6f7a8b-9c0d-1e2f-3a4b-5c6d7e8f9a0b.png'),
    ('大型会议室5', 'LARGE', 28, 'B栋3层303', 'AVAILABLE', '/resources/6f7a8b9c-0d1e-2f3a-4b5c-6d7e8f9a0b1c.jpg'),
    ('大型会议室6', 'LARGE', 45, 'B栋4层401', 'AVAILABLE', '/resources/7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c2d.jpeg');