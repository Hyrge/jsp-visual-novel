package dao;

import dto.Player;

public class PlayerDAO {
    
    public Long createPlayer(String savePath) {
        return null;
    }
    
    public Player findById(Long pid) {
        return null;
    }
    
    public String getSavePath(Long pid) {
        return null;
    }
    
    public void updateLastAccess(Long pid) {
    }
    
    // 상태 업데이트 (PLAYING → CLEAR)
    public void updateState(Long pid, String state) {
    }
    
    // 만료된 플레이어 삭제
    public void deleteExpired() {
    }
    
    // 플레이어 존재 여부 확인
    public boolean exists(Long pid) {
        return false;
    }
    
}