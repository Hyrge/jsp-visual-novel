package dto;

import java.time.LocalDateTime;

public class Player {
    private String pid; // VARCHAR(50) - UUID 문자열
    private String savePath;
    private LocalDateTime lastAccess;
    private String state; // ENUM('PLAYING', 'CLEAR')

    public Player() {
    }

    public Player(String pid, String savePath) {
        this.pid = pid;
        this.savePath = savePath;
        this.lastAccess = LocalDateTime.now();
        this.state = "PLAYING";
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

