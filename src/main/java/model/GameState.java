package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import dto.Comment;
import manager.DataManager;
import service.PostService;
import util.NPCReactionManager;
import util.NPCReactionManager.NPCReactionResult;

public class GameState {
    private int reputation = 20;

    // 게임 내 현재 날짜 (초기값: 2025-09-01)
    private LocalDate currentDate = LocalDate.of(2025, 9, 1);

    // 게임 내 현재 시간 (예: 오전 9시 시작)
    private LocalTime currentTime = LocalTime.of(9, 0);

    // 게임의 목표 날짜 (고정값: 2025-12-03)
    private LocalDate targetDate = LocalDate.of(2025, 12, 3);

    public GameState() {
    }

    public void addReputation(int value) {
        int before = this.reputation;
        this.reputation = Math.max(0, Math.min(100, reputation + value));
        EventBus.getInstance().emit("REPUTATION_CHANGED", Map.of(
                "before", before,
                "after", reputation,
                "delta", value));
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public LocalTime getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(LocalTime currentTime) {
        this.currentTime = currentTime;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    /**
     * 앨범 발매일까지 남은 일수 계산
     * @return D-Day 텍스트 (예: "D-93", "D-Day", "D+7")
     */
    public String getDDayText() {
        long daysUntilRelease = java.time.temporal.ChronoUnit.DAYS.between(currentDate, targetDate);
        if (daysUntilRelease > 0) {
            return "D-" + daysUntilRelease;
        } else if (daysUntilRelease == 0) {
            return "D-Day";
        } else {
            return "D+" + Math.abs(daysUntilRelease);
        }
    }

    /**
     * 앨범 발매일까지 남은 일수 반환
     * @return 남은 일수 (음수일 경우 발매 이후)
     */
    public long getDaysUntilRelease() {
        return java.time.temporal.ChronoUnit.DAYS.between(currentDate, targetDate);
    }

    /**
     * 현재 게임 날짜와 시간을 LocalDateTime으로 반환
     * @return 현재 게임 날짜시간
     */
    public java.time.LocalDateTime getCurrentDateTime() {
        return java.time.LocalDateTime.of(currentDate, currentTime);
    }

    /**
     * 게임 시간을 분 단위로 진행
     * @param minutes 진행할 분
     */
    public void advanceTime(int minutes) {
        LocalDateTime current = getCurrentDateTime();
        LocalDateTime advanced = current.plusMinutes(minutes);
        this.currentDate = advanced.toLocalDate();
        this.currentTime = advanced.toLocalTime();
    }

    /**
     * 다음 이벤트 시간으로 점프하고 해당 이벤트들 처리
     * @param playerPid 플레이어 PID (댓글 저장용)
     * @return 처리된 이벤트 수
     */
    public int advanceToNextEvent(String playerPid) {
        NPCReactionManager reactionManager = NPCReactionManager.getInstance();
        
        // 다음 이벤트 시간 확인
        LocalDateTime nextEventTime = reactionManager.getNextReactionTime();
        if (nextEventTime == null) {
            System.out.println("[GameState] 예약된 이벤트가 없습니다.");
            return 0;
        }
        
        // 시간 점프
        this.currentDate = nextEventTime.toLocalDate();
        this.currentTime = nextEventTime.toLocalTime();
        System.out.println("[GameState] 시간 점프: " + nextEventTime);
        
        // 해당 시간까지의 이벤트 처리
        List<NPCReactionResult> results = reactionManager.processReactions(nextEventTime);
        
        // 댓글 이벤트 DB 저장
        PostService postService = new PostService(DataManager.getInstance());
        int savedCount = 0;
        
        for (NPCReactionResult result : results) {
            if (result.getType() == NPCReactionManager.NPCReactionType.COMMENT && result.getGeneratedText() != null) {
                try {
                    String postId = (String) result.getOriginalParameters().get("postId");
                    
                    Comment comment = new Comment();
                    comment.setPostId(postId);
                    comment.setPlayerPid(playerPid);
                    comment.setContent(result.getGeneratedText());
                    comment.setCreatedAt(result.getExecutedTime());
                    
                    // NPC 닉네임 설정
                    String npcNickname = postService.assignNicknameForNPC(result.getNpcId(), postId);
                    comment.setAuthorNickname(npcNickname);
                    
                    if (postService.createComment(comment)) {
                        savedCount++;
                        System.out.println("[GameState] NPC 댓글 저장: " + npcNickname);
                    }
                } catch (Exception e) {
                    System.err.println("[GameState] 댓글 저장 오류: " + e.getMessage());
                }
            }
        }
        
        System.out.println("[GameState] " + savedCount + "개의 이벤트 처리됨");
        return savedCount;
    }

    /**
     * 다음 이벤트까지 남은 시간(분) 반환
     * @return 남은 분, 이벤트 없으면 -1
     */
    public long getMinutesToNextEvent() {
        NPCReactionManager reactionManager = NPCReactionManager.getInstance();
        LocalDateTime nextEventTime = reactionManager.getNextReactionTime();
        
        if (nextEventTime == null) {
            return -1;
        }
        
        return java.time.Duration.between(getCurrentDateTime(), nextEventTime).toMinutes();
    }

    /**
     * 다음 이벤트 시간 조회
     * @return 다음 이벤트 시간, 없으면 null
     */
    public LocalDateTime getNextEventTime() {
        return NPCReactionManager.getInstance().getNextReactionTime();
    }
}
