package service;

import java.time.LocalDateTime;
import java.util.Map;

import model.EventBus;
import model.GameContext;
import model.GameState;
import model.entity.UserAction;
import model.enums.ActionType;
import model.enums.BusEvent;
import util.LLMManager;

/**
 * 플레이어 행동에 대한 시스템 반응을 처리하는 핸들러
 * - 게임 시간 진행
 * - NPC 반응 생성
 * - 여론 변화
 * - 이벤트 트리거
 * - 퀘스트 업데이트 등
 */
public class UserActionHandler {
    private final GameContext gameContext;
    private final LLMManager llmManager;

    public UserActionHandler(GameContext gameContext) {
        this.gameContext = gameContext;
        this.llmManager = LLMManager.getInstance();
    }

    /**
     * 플레이어 행동 처리
     * @param action 플레이어 행동
     */
    public void handle(UserAction action) {
        if (action == null || gameContext == null) {
            return;
        }

        System.out.println("[UserActionHandler] 처리 시작: " + action.getActionType());

        try {
            // 1. 게임 시간 진행
            advanceGameTime(action);

            // 2. 액션 타입에 따른 처리
            switch (action.getActionType()) {
                case CREATE_POST:
                    handleCreatePost(action);
                    break;

                case CREATE_COMMENT:
                case CREATE_REPLY:
                    handleCreateComment(action);
                    break;

                case UPDATE_POST:
                    handleUpdatePost(action);
                    break;

                case DELETE_POST:
                    handleDeletePost(action);
                    break;

                case LIKE:
                case DISLIKE:
                    handleReaction(action);
                    break;

                case REPORT:
                    handleReport(action);
                    break;

                default:
                    System.out.println("[UserActionHandler] 알 수 없는 액션: " + action.getActionType());
                    break;
            }

            // 3. 여론 변화 체크 (필요 시)
            checkReputationChange(action);

            // 4. 이벤트 트리거 체크
            checkEventTriggers(action);

            // 5. 퀘스트 진행 상황 업데이트
            updateQuestProgress(action);

        } catch (Exception e) {
            System.err.println("[UserActionHandler] 처리 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 게임 시간 진행
     */
    private void advanceGameTime(UserAction action) {
        GameState gameState = gameContext.getGameState();

        int minutes = 0;
        switch (action.getActionType()) {
            case CREATE_POST:
                minutes = 10; // 게시글 작성 10분
                break;
            case CREATE_COMMENT:
            case CREATE_REPLY:
                minutes = 3;  // 댓글/대댓글 3분
                break;
            case UPDATE_POST:
                minutes = 5;  // 수정 5분
                break;
            default:
                minutes = 1;  // 기타 행동 1분
                break;
        }

        gameState.advanceTime(minutes);
        System.out.println("[UserActionHandler] 게임 시간 " + minutes + "분 진행");
    }

    /**
     * 게시글 작성 처리 (DB 저장만)
     */
    private void handleCreatePost(UserAction action) {
        PostService postService = gameContext.getPostService();

        // DB에 게시글 저장 (이미 postActions.jsp에서 저장되었으므로 여기서는 추가 작업 없음)
        System.out.println("[UserActionHandler] 게시글 작성 처리 완료: " + action.getTitle());

        // 비동기로 MiNa 관련 여부 확인 → POST_CREATED 이벤트 발행
        checkMinaRelatedAsync(action);
    }

    /**
     * 댓글/대댓글 작성 처리
     */
    private void handleCreateComment(UserAction action) {
        // TODO: 댓글에 대한 NPC 대댓글 생성 로직
        System.out.println("[UserActionHandler] 댓글 반응 처리 예정");
    }

    /**
     * 게시글 수정 처리
     */
    private void handleUpdatePost(UserAction action) {
        // TODO: 게시글 수정에 대한 반응
        System.out.println("[UserActionHandler] 게시글 수정 처리 예정");
    }

    /**
     * 게시글 삭제 처리
     */
    private void handleDeletePost(UserAction action) {
        // TODO: 게시글 삭제에 대한 반응 (여론 변화 등)
        System.out.println("[UserActionHandler] 게시글 삭제 처리 예정");
    }

    /**
     * 좋아요/싫어요 처리
     */
    private void handleReaction(UserAction action) {
        // TODO: 추천/비추천 반응
        System.out.println("[UserActionHandler] 반응 처리 예정");
    }

    /**
     * 신고 처리
     */
    private void handleReport(UserAction action) {
        // TODO: 신고 처리 로직
        System.out.println("[UserActionHandler] 신고 처리 예정");
    }

    /**
     * 여론 변화 체크
     */
    private void checkReputationChange(UserAction action) {
        // TODO: 특정 행동에 따른 여론 변화 로직
    }

    /**
     * 이벤트 트리거 체크
     */
    private void checkEventTriggers(UserAction action) {
        // TODO: 행동에 따른 이벤트 발생 확인
        // 예: "MiNa 관련 게시글 10개 작성" → 특정 이벤트 트리거
    }

    /**
     * 퀘스트 진행 상황 업데이트
     */
    private void updateQuestProgress(UserAction action) {
        // TODO: 퀘스트 시스템과 연동
        // 예: "게시글 5개 작성하기" 퀘스트 진행도 업데이트
    }

    /**
     * MiNa 관련 여부를 비동기로 확인하고 POST_CREATED 이벤트 발행
     */
    private void checkMinaRelatedAsync(UserAction action) {
        if (action.getActionType() != ActionType.CREATE_POST) {
            return;
        }

        PostService postService = gameContext.getPostService();
        EventBus eventBus = gameContext.getEventBus();

        // 별도 스레드에서 LLM 호출
        new Thread(() -> {
            try {
                boolean isRelatedMina = llmManager.isRelatedToMina(action.getTitle(), action.getContent());

                // DB 업데이트
                postService.updatePostMinaRelated(action.getTargetId(), isRelatedMina);

                System.out.println("[UserActionHandler] MiNa 관련 여부 확인 완료: "
                    + action.getTargetId() + " = " + isRelatedMina);

                // POST_CREATED 이벤트 발행 (NPC 댓글 생성 트리거)
                eventBus.emit(BusEvent.POST_CREATED, Map.of(
                    "postId", action.getTargetId(),
                    "title", action.getTitle(),
                    "content", action.getContent(),
                    "playerId", action.getPlayerId(),
                    "isRelatedMina", isRelatedMina
                ));
                System.out.println("[UserActionHandler] POST_CREATED 이벤트 발행 (isRelatedMina: " + isRelatedMina + ")");

            } catch (Exception e) {
                System.err.println("[UserActionHandler] MiNa 관련 여부 확인 실패: " + e.getMessage());

                // 실패해도 이벤트는 발행 (isRelatedMina = false로)
                eventBus.emit(BusEvent.POST_CREATED, Map.of(
                    "postId", action.getTargetId(),
                    "title", action.getTitle(),
                    "content", action.getContent(),
                    "playerId", action.getPlayerId(),
                    "isRelatedMina", false
                ));
            }
        }).start();
    }
}
