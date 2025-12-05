package util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.HashMap;

import dto.Comment;
import manager.DataManager;
import service.PostService;

/**
 * NPC 반응 큐 시스템 관리
 * 플레이어 행동에 대한 NPC의 시간차 반응을 처리
 */
public class NPCReactionManager {
    private static NPCReactionManager instance;
    private LLMManager llmManager;
    private Random random;

    // 반응 큐: 예정 시각과 반응 이벤트를 저장
    private PriorityQueue<NPCReaction> reactionQueue;

    private NPCReactionManager() {
        this.llmManager = LLMManager.getInstance();
        this.random = new Random();
        // 시간순으로 정렬되는 우선순위 큐
        this.reactionQueue = new PriorityQueue<>(Comparator.comparing(NPCReaction::getScheduledTime));
    }

    public static NPCReactionManager getInstance() {
        if (instance == null) {
            synchronized (NPCReactionManager.class) {
                if (instance == null) {
                    instance = new NPCReactionManager();
                }
            }
        }
        return instance;
    }

    /**
     * 플레이어의 게시글에 대한 NPC 댓글 반응 예약
     * @param postId 게시글 ID
     * @param postTitle 게시글 제목
     * @param postContent 게시글 내용
     * @param currentTime 현재 게임 시간
     * @param currentSentiment 현재 여론
     */
    public void scheduleCommentReactions(String postId, String postTitle, String postContent,
                                         LocalDateTime currentTime, int currentSentiment) {
        // 랜덤하게 1~3명의 NPC가 반응
        int numReactions = 1 + random.nextInt(3);
        List<Map<String, Object>> allProfiles = llmManager.getAllNPCProfiles();

        if (allProfiles == null || allProfiles.isEmpty()) {
            System.err.println("NPCReactionManager: NPC 프로필이 없습니다");
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

            // 반응 시간 계산
            LocalDateTime scheduledTime = calculateReactionTime(currentTime, isOnline);

            // 반응 확률 체크
            double reactionProbability = isOnline ? 0.7 : 0.5;
            if (random.nextDouble() > reactionProbability) {
                continue; // 이번엔 반응 안 함
            }

            // 반응 큐에 추가
            NPCReaction reaction = new NPCReaction(
                npcId,
                NPCReactionType.COMMENT,
                scheduledTime,
                Map.of(
                    "postId", postId,
                    "postTitle", postTitle,
                    "postContent", postContent,
                    "sentiment", currentSentiment
                )
            );

            reactionQueue.offer(reaction);
            System.out.println("NPCReactionManager: NPC 댓글 반응 예약 - " + npcId + " at " + scheduledTime);
        }
    }

    /**
     * 이슈에 대한 NPC 게시글 반응 예약
     * @param topic 이슈 주제
     * @param currentTime 현재 게임 시간
     * @param currentSentiment 현재 여론
     */
    public void schedulePostReaction(String topic, LocalDateTime currentTime, int currentSentiment) {
        // 랜덤 NPC 선택
        Map<String, Object> profile = llmManager.getRandomNPCProfile();
        if (profile == null) {
            System.err.println("NPCReactionManager: NPC 프로필을 가져올 수 없습니다");
            return;
        }

        String npcId = (String) profile.get("id");
        boolean isOnline = isNPCOnline(profile, currentTime);

        // 게시글 작성은 더 오래 걸림
        LocalDateTime scheduledTime = calculatePostCreationTime(currentTime, isOnline);

        NPCReaction reaction = new NPCReaction(
            npcId,
            NPCReactionType.POST,
            scheduledTime,
            Map.of(
                "topic", topic,
                "sentiment", currentSentiment
            )
        );

        reactionQueue.offer(reaction);
        System.out.println("NPCReactionManager: NPC 게시글 반응 예약 - " + npcId + " at " + scheduledTime);
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

    /**
     * 게시글 작성 시간 계산
     */
    private LocalDateTime calculatePostCreationTime(LocalDateTime currentTime, boolean isOnline) {
        if (isOnline) {
            // 온라인: 5~15분 후
            int minutes = 5 + random.nextInt(11);
            return currentTime.plusMinutes(minutes);
        } else {
            // 오프라인: 2~8시간 후
            int hours = 2 + random.nextInt(7);
            return currentTime.plusHours(hours);
        }
    }

    /**
     * 예정된 반응들 중 현재 시간 이전의 것들을 처리
     * @param currentTime 현재 게임 시간
     * @return 처리된 반응 목록
     */
    public List<NPCReactionResult> processReactions(LocalDateTime currentTime) {
        List<NPCReactionResult> results = new ArrayList<>();

        while (!reactionQueue.isEmpty() && !reactionQueue.peek().getScheduledTime().isAfter(currentTime)) {
            NPCReaction reaction = reactionQueue.poll();
            NPCReactionResult result = executeReaction(reaction);
            if (result != null) {
                results.add(result);
            }
        }

        return results;
    }

    /**
     * 반응 실행
     */
    private NPCReactionResult executeReaction(NPCReaction reaction) {
        String npcId = reaction.getNpcId();
        Map<String, Object> params = reaction.getParameters();

        try {
            if (reaction.getType() == NPCReactionType.COMMENT) {
                // 댓글 생성
                String postTitle = (String) params.get("postTitle");
                String postContent = (String) params.get("postContent");
                int sentiment = (Integer) params.get("sentiment");

                String comment = llmManager.generateComment(npcId, postTitle, postContent, sentiment);

                if (comment != null) {
                    return new NPCReactionResult(
                        npcId,
                        NPCReactionType.COMMENT,
                        reaction.getScheduledTime(),
                        comment,
                        params
                    );
                }
            } else if (reaction.getType() == NPCReactionType.POST) {
                // 게시글 생성
                String topic = (String) params.get("topic");
                int sentiment = (Integer) params.get("sentiment");

                Map<String, String> post = llmManager.generatePost(npcId, topic, sentiment);

                if (post != null) {
                    return new NPCReactionResult(
                        npcId,
                        NPCReactionType.POST,
                        reaction.getScheduledTime(),
                        post.get("title") + "\n---\n" + post.get("content"),
                        params
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("NPCReactionManager: 반응 실행 중 오류 - " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 다음 반응 예정 시각 조회
     */
    public LocalDateTime getNextReactionTime() {
        if (reactionQueue.isEmpty()) {
            return null;
        }
        return reactionQueue.peek().getScheduledTime();
    }

    /**
     * 반응 큐 크기 조회
     */
    public int getQueueSize() {
        return reactionQueue.size();
    }

    /**
     * 반응 큐 초기화 (새 게임 시작 시)
     */
    public void clearQueue() {
        reactionQueue.clear();
    }

    // 내부 클래스: NPC 반응 데이터
    private static class NPCReaction {
        private String npcId;
        private NPCReactionType type;
        private LocalDateTime scheduledTime;
        private Map<String, Object> parameters;

        public NPCReaction(String npcId, NPCReactionType type, LocalDateTime scheduledTime, Map<String, Object> parameters) {
            this.npcId = npcId;
            this.type = type;
            this.scheduledTime = scheduledTime;
            this.parameters = parameters;
        }

        public String getNpcId() {
            return npcId;
        }

        public NPCReactionType getType() {
            return type;
        }

        public LocalDateTime getScheduledTime() {
            return scheduledTime;
        }

        public Map<String, Object> getParameters() {
            return parameters;
        }
    }

    // 반응 타입 enum
    public enum NPCReactionType {
        COMMENT,    // 댓글
        POST        // 게시글
    }

    // 반응 결과 클래스
    public static class NPCReactionResult {
        private String npcId;
        private NPCReactionType type;
        private LocalDateTime executedTime;
        private String generatedText;
        private Map<String, Object> originalParameters;

        public NPCReactionResult(String npcId, NPCReactionType type, LocalDateTime executedTime,
                                String generatedText, Map<String, Object> originalParameters) {
            this.npcId = npcId;
            this.type = type;
            this.executedTime = executedTime;
            this.generatedText = generatedText;
            this.originalParameters = originalParameters;
        }

        public String getNpcId() {
            return npcId;
        }

        public NPCReactionType getType() {
            return type;
        }

        public LocalDateTime getExecutedTime() {
            return executedTime;
        }

        public String getGeneratedText() {
            return generatedText;
        }

        public Map<String, Object> getOriginalParameters() {
            return originalParameters;
        }
    }
}
