-- ====================================
-- 데이터베이스 초기화 스크립트
-- ====================================

-- 기존 테이블 삭제
DROP TABLE IF EXISTS player;

-- 테이블 생성
CREATE TABLE player (
    pid BIGINT PRIMARY KEY AUTO_INCREMENT,
    save_path VARCHAR(100) NOT NULL COMMENT '저장 폴더 경로 (saves/1)',
    last_access DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '마지막 접속 시간',
    state ENUM('PLAYING', 'CLEAR') NOT NULL DEFAULT 'PLAYING' COMMENT '게임 상태',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='플레이어 정보';

-- 인덱스 생성
CREATE INDEX idx_player_state ON player(state);
CREATE INDEX idx_player_last_access ON player(last_access);


