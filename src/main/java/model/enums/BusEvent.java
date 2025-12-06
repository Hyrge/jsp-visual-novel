package model.enums;

/**
 * EventBus에서 사용하는 이벤트 이름 정의
 */
public enum BusEvent {
    // 게임 상태 이벤트
    REPUTATION_CHANGED,      // 여론 변화
    TIME_ADVANCED,           // 시간 진행
    DAY_CHANGED,             // 날짜 변경
    // DAILY_SETTLEMENT,        // 일일 정산

    // 플레이어 행동 이벤트
    POST_CREATED,            // 게시글 작성
    COMMENT_CREATED,         // 댓글 작성
    // POST_DELETED,            // 게시글 삭제

    // NPC 반응 이벤트
    NPC_COMMENT_CREATED,     // NPC 댓글 생성
    NPC_POST_CREATED,        // NPC 게시글 생성

    // 퀘스트 이벤트
    QUEST_COMPLETED,         // 퀘스트 완료
    QUEST_ADDED,             // 퀘스트 추가
    QUEST_PROGRESS_UPDATED,  // 퀘스트 진행도 업데이트

    // 메시지 이벤트
    MESSAGE_SENT,            // 쪽지 전송

    // 게임 이벤트 트리거
    EVENT_TRIGGERED          // 스크립트 이벤트 발생
}
