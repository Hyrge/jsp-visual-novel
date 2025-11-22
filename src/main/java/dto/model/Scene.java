package model;

import java.util.List;

/**
 * 씬(장면) 정보를 담는 모델 클래스
 */
public class Scene {
    private int id;
    private String background;
    private String character;
    private String image;
    private String dialogue;
    private List<Choice> choices;

    // 기본 생성자
    public Scene() {}

    // 전체 필드 생성자
    public Scene(int id, String background, String character, String image, String dialogue, List<Choice> choices) {
        this.id = id;
        this.background = background;
        this.character = character;
        this.image = image;
        this.dialogue = dialogue;
        this.choices = choices;
    }

    // Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDialogue() {
        return dialogue;
    }

    public void setDialogue(String dialogue) {
        this.dialogue = dialogue;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    /**
     * 캐릭터 이름을 한글로 반환
     */
    public String getCharacterName() {
        if ("mina".equals(character)) {
            return "미나";
        } else if ("kangwoo".equals(character)) {
            return "강우";
        }
        return character;
    }

    @Override
    public String toString() {
        return "Scene{" +
                "id=" + id +
                ", background='" + background + '\'' +
                ", character='" + character + '\'' +
                ", dialogue='" + dialogue + '\'' +
                '}';
    }
}
