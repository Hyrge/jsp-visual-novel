package model;

import java.time.LocalTime;
import java.util.List;

/**
 * NPC 사용자 프로필을 나타내는 모델 클래스
 * profile_config.json의 데이터를 객체화
 */
public class NPCUser {
    private String id;                      // NPC 고유 ID (예: "4f91ac")
    private String templateName;            // 템플릿 이름 (예: "여고생 A")
    private String npcType;                 // NPC 유형 (학생, 직장인, 주부, 무직, 대학생, 프리랜서)
    private String description;             // 설명
    private int baseSentiment;              // 기본 성향 (-10 ~ +10, 음수=안티, 양수=팬)
    private String signupDate;              // 가입일 (YYYY-MM-DD)
    private String activeTimeStart;         // 활동 시작 시간 (HH:mm)
    private String activeTimeEnd;           // 활동 종료 시간 (HH:mm)
    private int avgSessionMinutes;          // 평균 세션 시간 (분)
    private int responseSpeed;              // 반응 속도 (1=빠름, 2=보통, 3=느림, 4=매우느림)
    private String speechStyle;             // 말투 스타일
    private String personalityDesc;         // 성격 설명
    private List<String> nicknamePool;      // 닉네임 후보군

    // 기본 생성자
    public NPCUser() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getNpcType() {
        return npcType;
    }

    public void setNpcType(String npcType) {
        this.npcType = npcType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBaseSentiment() {
        return baseSentiment;
    }

    public void setBaseSentiment(int baseSentiment) {
        this.baseSentiment = baseSentiment;
    }

    public String getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(String signupDate) {
        this.signupDate = signupDate;
    }

    public String getActiveTimeStart() {
        return activeTimeStart;
    }

    public void setActiveTimeStart(String activeTimeStart) {
        this.activeTimeStart = activeTimeStart;
    }

    public String getActiveTimeEnd() {
        return activeTimeEnd;
    }

    public void setActiveTimeEnd(String activeTimeEnd) {
        this.activeTimeEnd = activeTimeEnd;
    }

    public int getAvgSessionMinutes() {
        return avgSessionMinutes;
    }

    public void setAvgSessionMinutes(int avgSessionMinutes) {
        this.avgSessionMinutes = avgSessionMinutes;
    }

    public int getResponseSpeed() {
        return responseSpeed;
    }

    public void setResponseSpeed(int responseSpeed) {
        this.responseSpeed = responseSpeed;
    }

    public String getSpeechStyle() {
        return speechStyle;
    }

    public void setSpeechStyle(String speechStyle) {
        this.speechStyle = speechStyle;
    }

    public String getPersonalityDesc() {
        return personalityDesc;
    }

    public void setPersonalityDesc(String personalityDesc) {
        this.personalityDesc = personalityDesc;
    }

    public List<String> getNicknamePool() {
        return nicknamePool;
    }

    public void setNicknamePool(List<String> nicknamePool) {
        this.nicknamePool = nicknamePool;
    }

    // 유틸리티 메서드

    /**
     * 현재 시간에 NPC가 온라인 상태인지 확인
     * @param currentTime 현재 게임 시간 (HH:mm 형식)
     * @return 온라인이면 true, 오프라인이면 false
     */
    public boolean isOnline(String currentTime) {
        try {
            LocalTime current = LocalTime.parse(currentTime);
            LocalTime start = LocalTime.parse(activeTimeStart);
            LocalTime end = LocalTime.parse(activeTimeEnd);

            // 자정을 넘어가는 경우 처리 (예: 18:00 ~ 02:00)
            if (start.isBefore(end)) {
                return !current.isBefore(start) && !current.isAfter(end);
            } else {
                return !current.isBefore(start) || !current.isAfter(end);
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 닉네임 풀에서 랜덤으로 닉네임 선택
     * @return 랜덤 닉네임
     */
    public String getRandomNickname() {
        if (nicknamePool == null || nicknamePool.isEmpty()) {
            return "유저";
        }
        int index = (int) (Math.random() * nicknamePool.size());
        return nicknamePool.get(index);
    }

    /**
     * 반응 속도에 따른 지연 시간 계산 (분 단위)
     * @param isOnline 온라인 상태 여부
     * @return 지연 시간 (분)
     */
    public int getDelayMinutes(boolean isOnline) {
        if (isOnline) {
            // 온라인: 1~5분
            return 1 + (int) (Math.random() * 5);
        } else {
            // 오프라인: 60~300분 (1~5시간)
            return 60 + (int) (Math.random() * 240);
        }
    }

    /**
     * NPC가 안티인지 확인
     * @return 안티면 true (baseSentiment < 0)
     */
    public boolean isAnti() {
        return baseSentiment < 0;
    }

    /**
     * NPC가 팬인지 확인
     * @return 팬이면 true (baseSentiment > 3)
     */
    public boolean isFan() {
        return baseSentiment > 3;
    }

    @Override
    public String toString() {
        return "NPCUser{" +
                "id='" + id + '\'' +
                ", templateName='" + templateName + '\'' +
                ", npcType='" + npcType + '\'' +
                ", baseSentiment=" + baseSentiment +
                ", activeTime=" + activeTimeStart + "~" + activeTimeEnd +
                '}';
    }
}
