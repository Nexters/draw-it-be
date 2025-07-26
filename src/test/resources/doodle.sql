TRUNCATE TABLE doodles;
TRUNCATE TABLE projects;

INSERT INTO projects (id, user_id, topic, message, background_color, is_deleted, uuid, created_at, updated_at) 
VALUES (1, 1, '테스트 프로젝트', '테스트 메시지', '#FFFFFF', false, 'test-project-uuid', NOW(), NOW());