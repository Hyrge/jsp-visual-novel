package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import dto.Player;
import util.DBUtil;

/**
 * Player 테이블 DAO
 */
public class PlayerDAO {

    /**
     * 플레이어 생성 (자동 회원가입)
     * @param pid UUID 문자열
     * @return 생성 성공 여부
     */
    public boolean createPlayer(String pid) {
        String sql = "INSERT INTO player (pid, last_access, state) VALUES (?, NOW(), 'PLAYING')";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pid);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * PID로 플레이어 조회
     */
    public Player findById(String pid) {
        String sql = "SELECT pid, last_access, state FROM player WHERE pid = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pid);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Player player = new Player();
                    player.setPid(rs.getString("pid"));

                    Timestamp timestamp = rs.getTimestamp("last_access");
                    if (timestamp != null) {
                        player.setLastAccess(timestamp.toLocalDateTime());
                    }

                    player.setState(rs.getString("state"));
                    return player;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 마지막 접속 시간 업데이트
     */
    public void updateLastAccess(String pid) {
        String sql = "UPDATE player SET last_access = NOW() WHERE pid = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pid);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 상태 업데이트 (PLAYING → CLEAR)
     */
    public void updateState(String pid, String state) {
        String sql = "UPDATE player SET state = ? WHERE pid = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, state);
            pstmt.setString(2, pid);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 만료된 플레이어 삭제 (30일 이상 접속하지 않은 플레이어)
     */
    public void deleteExpired() {
        String sql = "DELETE FROM player WHERE last_access < DATE_SUB(NOW(), INTERVAL 30 DAY)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int deleted = pstmt.executeUpdate();
            System.out.println("만료된 플레이어 " + deleted + "명 삭제됨");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 플레이어 존재 여부 확인
     */
    public boolean exists(String pid) {
        String sql = "SELECT COUNT(*) FROM player WHERE pid = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pid);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
