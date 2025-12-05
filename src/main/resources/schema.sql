-- ====================================
-- 데이터베이스 초기화 스크립트
-- ====================================

DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS player;

-- 1. player 테이블 (기존 유지)
CREATE TABLE IF NOT EXISTS player (
    pid VARCHAR(50) PRIMARY KEY,
    last_access DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    state ENUM('PLAYING', 'CLEAR') NOT NULL DEFAULT 'PLAYING'
);

CREATE INDEX IF NOT EXISTS idx_player_state ON player(state);
CREATE INDEX IF NOT EXISTS idx_player_last_access ON player(last_access);

-- 2. users 테이블 (플레이어 + NPC 통합)
CREATE TABLE IF NOT EXISTS users (
    pid VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50),
    password VARCHAR(50),
    nickname VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    bio TEXT
);

-- 3. posts 테이블 (게시글)
CREATE TABLE IF NOT EXISTS posts (
    post_id VARCHAR(30) PRIMARY KEY,
    player_pid VARCHAR(50) COMMENT '작성자 PID',
    title VARCHAR(200) COMMENT '제목',
    content TEXT COMMENT '내용',
    board_type VARCHAR(20) COMMENT '게시판 종류 (talk, report)',
    category VARCHAR(20) COMMENT '카테고리',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',
    has_pictures BOOLEAN DEFAULT FALSE,
    like_count INT DEFAULT 0,
    dislike_count INT DEFAULT 0,
    is_related_mina BOOLEAN DEFAULT FALSE,
    image_file VARCHAR(255) COMMENT '이미지 파일명 (saves/{pid}/ 경로에 저장)',
    FOREIGN KEY (player_pid) REFERENCES player(pid) ON DELETE CASCADE
);

-- 4. comments 테이블 (댓글)
CREATE TABLE IF NOT EXISTS comments (
    comment_id INT PRIMARY KEY AUTO_INCREMENT,
    comment_seq INT,
    post_id VARCHAR(10) COMMENT '게시글 ID',
    player_pid VARCHAR(50) COMMENT '작성자 PID',
    content TEXT COMMENT '댓글 내용',
    parent_comment_id INT COMMENT '부모 댓글 ID (답글)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (player_pid) REFERENCES player(pid) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES comments(comment_id) ON DELETE CASCADE
);
