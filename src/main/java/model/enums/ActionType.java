package model.enums;

public enum ActionType {
    CREATE_POST,     // 게시글 작성
    CREATE_COMMENT,  // 댓글 작성
    CREATE_REPLY,    // 대댓글 작성
    UPDATE_POST,     // 게시글 수정
    DELETE_POST,     // 게시글 삭제
    LIKE,            // 좋아요
    DISLIKE,         // 싫어요
    REPORT           // 신고
}