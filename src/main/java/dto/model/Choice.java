package model;

import java.util.Map;

/**
 * 선택지 정보를 담는 모델 클래스
 */
public class Choice {
    private String text;
    private Integer nextScene;
    private Map<String, Integer> affection;  // {"mina": 5, "kangwoo": 10}
    private String route;

    // 기본 생성자
    public Choice() {}

    // 전체 필드 생성자
    public Choice(String text, Integer nextScene, Map<String, Integer> affection, String route) {
        this.text = text;
        this.nextScene = nextScene;
        this.affection = affection;
        this.route = route;
    }

    // Getter & Setter
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getNextScene() {
        return nextScene;
    }

    public void setNextScene(Integer nextScene) {
        this.nextScene = nextScene;
    }

    public Map<String, Integer> getAffection() {
        return affection;
    }

    public void setAffection(Map<String, Integer> affection) {
        this.affection = affection;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    /**
     * 미나 호감도 변화량 반환
     */
    public int getMinaAffection() {
        if (affection != null && affection.containsKey("mina")) {
            return affection.get("mina");
        }
        return 0;
    }

    /**
     * 강우 호감도 변화량 반환
     */
    public int getKangwooAffection() {
        if (affection != null && affection.containsKey("kangwoo")) {
            return affection.get("kangwoo");
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Choice{" +
                "text='" + text + '\'' +
                ", nextScene=" + nextScene +
                '}';
    }
}
