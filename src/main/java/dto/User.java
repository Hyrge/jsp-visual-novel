package dto;

import java.time.LocalDateTime;

/**
 * 회원 정보 DTO (JavaBean)
 * UseBean 패턴을 위해 기본 생성자와 getter/setter 필수
 */
public class User {
    private String pid;           // PK (UUID)
    private String userId;        // 로그인 아이디
    private String password;      // 비밀번호
    private String nickname;      // 닉네임
    private LocalDateTime createdAt;  // 가입일
    private String bio;           // 자기소개

    // 기본 생성자 (UseBean 필수)
    public User() {
    }

    public User(String pid, String userId, String password, String nickname) {
        this.pid = pid;
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.createdAt = LocalDateTime.now();
    }

    // Getter & Setter
    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Override
    public String toString() {
        return "User{pid='" + pid + "', userId='" + userId + "', nickname='" + nickname + "'}";
    }
}

