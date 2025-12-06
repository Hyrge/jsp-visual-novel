package util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.HashMap;

import dto.Comment;
import manager.DataManager;
import model.EventBus;
import model.GameState;
import model.enums.BusEvent;
import service.PostService;

/**
 * NPC 댓글 생성 관리자
 * EventBus를 구독하여 POST_CREATED 이벤트 발생 시 NPC 댓글을 즉시 생성하고 DB에 저장
 */
public class NPCReactionManager {
    private LLMManager llmManager;
    private Random random;
    private EventBus eventBus;
    private GameState gameState;
    private PostService postService;
    private String playerPid;

    /**
     * 생성자 - EventBus를 주입받아 POST_CREATED 이벤트 구독
     */
    public NPCReactionManager(EventBus eventBus, GameState gameState, PostService postService, String playerPid) {
        this.llmManager = LLMManager.getInstance();
        this.random = new Random();
        this.eventBus = eventBus;
        this.gameState = gameState;
        this.postService = postService;
        this.playerPid = playerPid;

        // POST_CREATED 이벤트 구독 (플레이어 작성 + 기존 글이 보이게 됨)
        eventBus.subscribe(BusEvent.POST_CREATED, this::onPostCreated);
    }

    /**
     * POST_CREATED 이벤트 핸들러
     * 게시글이 생성되면 즉시 NPC 댓글을 생성하고 DB에 저장
     */
    private void onPostCreated(Object data) {
        Map<String, Object> postData = (Map<String, Object>) data;
        String postId = (String) postData.get("postId");
        String postTitle = (String) postData.get("title");
        String postContent = (String) postData.get("content");
        boolean isRelatedMina = (Boolean) postData.getOrDefault("isRelatedMina", false);

        LocalDateTime currentTime = gameState.getCurrentDateTime();
        int currentSentiment = gameState.getReputation();

        System.out.println("[NPCReactionManager] POST_CREATED 이벤트 수신: " + postTitle);

        // NPC 댓글 즉시 생성 및 DB 저장
        generateAndSaveComments(postId, postTitle, postContent, currentTime, currentSentiment, isRelatedMina);
    }

    /**
     * NPC 댓글을 즉시 생성하고 DB에 저장 (미래 시간으로)
     */
    private void generateAndSaveComments(String postId, String postTitle, String postContent,
                                         LocalDateTime currentTime, int currentSentiment, boolean isRelatedMina) {
        int numReactions = 8;
        List<Map<String, Object>> allProfiles = llmManager.getAllNPCProfiles();
        int savedCount = 0;

        if (allProfiles == null || allProfiles.isEmpty()) {
            System.err.println("[NPCReactionManager] NPC 프로필이 없습니다");
            return;
        }

        // 랜덤 NPC 선택 (중복 제거)
        Set<Integer> selectedIndices = new HashSet<>();
        while (selectedIndices.size() < numReactions && selectedIndices.size() < allProfiles.size()) {
            selectedIndices.add(random.nextInt(allProfiles.size()));
        }

        for (int index : selectedIndices) {
            Map<String, Object> profile = allProfiles.get(index);
            String npcId = (String) profile.get("id");

            // 온라인/오프라인 상태 확인
            boolean isOnline = isNPCOnline(profile, currentTime);

            // 반응 확률 체크
            double reactionProbability = isOnline ? 0.7 : 0.3;
            if(isRelatedMina) {
                reactionProbability += 0.1;
            }
            if (random.nextDouble() > reactionProbability) {
                continue; // 이번엔 반응 안 함
            }

            // 댓글이 작성될 시간 계산
            LocalDateTime commentTime = calculateReactionTime(currentTime, isOnline);

            // 즉시 LLM 호출하여 댓글 생성
            String commentText = llmManager.generateComment(npcId, postTitle, postContent, currentSentiment, isRelatedMina);

            if (commentText == null) {
                System.err.println("[NPCReactionManager] LLM 댓글 생성 실패: " + npcId);
                continue;
            }

            // DB에 저장 (미래 시간으로)
            Comment comment = new Comment();
            comment.setPostId(postId);
            comment.setPlayerPid(playerPid);
            comment.setContent(commentText);
            comment.setCreatedAt(commentTime);

            // NPC 닉네임 설정
            String npcNickname = postService.assignNicknameForNPC(npcId, postId);
            comment.setNickname(npcNickname);

            if (postService.createComment(comment)) {
                savedCount++;

                // GameState에 이벤트 시간 추가
                gameState.addEventTime(commentTime);

                System.out.println("[NPCReactionManager] NPC 댓글 생성 및 저장: " + npcNickname + " at " + commentTime);

                // NPC_COMMENT_CREATED 이벤트 발행
                eventBus.emit(BusEvent.NPC_COMMENT_CREATED, Map.of(
                    "npcId", npcId,
                    "postId", postId,
                    "commentTime", commentTime
                ));
            }
        }

        System.out.println("[NPCReactionManager] 총 " + savedCount + "개의 NPC 댓글 생성됨");
    }


    /**
     * NPC가 현재 온라인 상태인지 확인
     */
    private boolean isNPCOnline(Map<String, Object> profile, LocalDateTime currentTime) {
        String activeStart = (String) profile.get("activeTimeStart");
        String activeEnd = (String) profile.get("activeTimeEnd");

        LocalTime start = LocalTime.parse(activeStart);
        LocalTime end = LocalTime.parse(activeEnd);
        LocalTime now = currentTime.toLocalTime();

        // 시간대가 자정을 넘어가는 경우 처리
        if (start.isBefore(end)) {
            return !now.isBefore(start) && !now.isAfter(end);
        } else {
            // 예: 18:00 ~ 02:00
            return !now.isBefore(start) || !now.isAfter(end);
        }
    }

    /**
     * 댓글 반응 시간 계산
     */
    private LocalDateTime calculateReactionTime(LocalDateTime currentTime, boolean isOnline) {
        if (isOnline) {
            // 온라인: 1~5분 후
            int minutes = 1 + random.nextInt(5);
            return currentTime.plusMinutes(minutes);
        } else {
            // 오프라인: 1~5시간 후
            int hours = 1 + random.nextInt(5);
            return currentTime.plusHours(hours);
        }
    }

}
