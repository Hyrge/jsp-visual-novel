-- ====================================
-- 플레이어 만료 삭제 스크립트
-- ====================================
-- 클리어된 플레이어 삭제
DELETE FROM player WHERE state = 'CLEAR';

-- 10일 이상 미접속 플레이어 삭제
DELETE FROM player WHERE last_access < DATE_SUB(NOW(), INTERVAL 10 DAY);