-- ====================================
-- 데이터베이스 초기화 스크립트
-- ====================================

-- DROP TABLE IF EXISTS player;
-- DROP TABLE IF EXISTS users;
-- DROP TABLE IF EXISTS posts;
-- DROP TABLE IF EXISTS comments;

-- 1. player 테이블 (기존 유지)
CREATE TABLE player IF NOT EXISTS (
    pid VARCHAR(50) PRIMARY KEY,
    save_path VARCHAR(100) NOT NULL COMMENT '저장 폴더 경로 (saves/1)',
    last_access DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    state ENUM('PLAYING', 'CLEAR') NOT NULL DEFAULT 'PLAYING',
);

CREATE INDEX idx_player_state ON player(state);
CREATE INDEX idx_player_last_access ON player(last_access);

-- 2. users 테이블 (플레이어 + NPC 통합)
CREATE TABLE users IF NOT EXISTS (
    pid VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50),
    password VARCHAR(50),
    nickname VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    bio TEXT,
);

-- 3. posts 테이블 (게시글)
CREATE TABLE posts IF NOT EXISTS (
    post_id VARCHAR(30) PRIMARY KEY,
    author_pid VARCHAR(50) COMMENT '작성자 PID',
    title VARCHAR(200) COMMENT '제목',
    content TEXT COMMENT '내용',
    board_type VARCHAR(20) COMMENT '게시판 종류 (talk, report)',
    category VARCHAR(20) COMMENT '카테고리',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',
    FOREIGN KEY (author_pid) REFERENCES users(pid) ON DELETE CASCADE,
    has_pictures BOOLEAN DEFAULT FALSE,
    like_count INT DEFAULT 0,
    dislike_count INT DEFAULT 0,
);

-- 4. comments 테이블 (댓글)
CREATE TABLE comments IF NOT EXISTS (
    comment_id INT PRIMARY KEY AUTO_INCREMENT,
    comment_seq INT,
    post_id VARCHAR(10) COMMENT '게시글 ID',
    author_pid VARCHAR(50) COMMENT '작성자 PID',
    content TEXT COMMENT '댓글 내용',
    parent_comment_id VARCHAR(10) COMMENT '부모 댓글 ID (답글)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (author_pid) REFERENCES users(pid) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES comments(comment_id) ON DELETE CASCADE,
);

