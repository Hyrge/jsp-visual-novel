package dao;

import dto.User;
import util.DBUtil;

import java.sql.*;

/**
 * 회원 테이블 DAO
 */
public class UserDAO {

    /**
     * 회원가입
     */
    public boolean insert(User user) {
        String sql = "INSERT INTO users (pid, user_id, password, nickname, created_at, bio) VALUES (?, ?, ?, ?, NOW(), ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getPid());
            pstmt.setString(2, user.getUserId());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getNickname());
            pstmt.setString(5, user.getBio());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 아이디로 회원 조회
     */
    public User findByUserId(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * PID로 회원 조회
     */
    public User findByPid(String pid) {
        String sql = "SELECT * FROM users WHERE pid = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pid);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 아이디 중복 체크
     */
    public boolean existsByUserId(String userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);

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

    /**
     * 닉네임 중복 체크
     */
    public boolean existsByNickname(String nickname) {
        String sql = "SELECT COUNT(*) FROM users WHERE nickname = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nickname);

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

    /**
     * 로그인 검증
     */
    public User login(String userId, String password) {
        String sql = "SELECT * FROM users WHERE user_id = ? AND password = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * ResultSet -> User 객체 매핑
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setPid(rs.getString("pid"));
        user.setUserId(rs.getString("user_id"));
        user.setPassword(rs.getString("password"));
        user.setNickname(rs.getString("nickname"));
        user.setBio(rs.getString("bio"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            user.setCreatedAt(ts.toLocalDateTime());
        }

        return user;
    }
}

