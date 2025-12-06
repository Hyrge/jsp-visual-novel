package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import dto.Comment;
import util.DBUtil;

public class CommentDAO {

    public boolean insert(Comment comment) {
        String sql = "INSERT INTO comments (post_id, player_pid, content, " +
                     "parent_comment_id, created_at, nickname) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, comment.getPostId());
            pstmt.setString(2, comment.getPlayerPid());
            pstmt.setString(3, comment.getContent());

            if (comment.getParentCommentId() != null) {
                pstmt.setInt(4, comment.getParentCommentId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setTimestamp(5, Timestamp.valueOf(comment.getCreatedAt()));
            pstmt.setString(6, comment.getNickname());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[CommentDAO] " + e.getMessage());
            return false;
        }
    }

    public List<Comment> findByPostId(String postId) {
        return findByPostId(postId, null, null);
    }

    public List<Comment> findByPostId(String postId, String playerPid) {
        return findByPostId(postId, playerPid, null);
    }

    /**
     * 게시글의 댓글 조회 (게임 시간 필터링 포함)
     * @param postId 게시글 ID
     * @param playerPid 플레이어 PID (null이면 전체 조회)
     * @param currentGameTime 현재 게임 시간 (null이면 필터링 안 함)
     */
    public List<Comment> findByPostId(String postId, String playerPid, java.time.LocalDateTime currentGameTime) {
        StringBuilder sql = new StringBuilder("SELECT * FROM comments WHERE post_id = ?");

        if (playerPid != null) {
            sql.append(" AND player_pid = ?");
        }

        if (currentGameTime != null) {
            sql.append(" AND created_at <= ?");
        }

        sql.append(" ORDER BY created_at ASC");

        List<Comment> comments = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            pstmt.setString(paramIndex++, postId);

            if (playerPid != null) {
                pstmt.setString(paramIndex++, playerPid);
            }

            if (currentGameTime != null) {
                pstmt.setTimestamp(paramIndex++, Timestamp.valueOf(currentGameTime));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapResultSetToComment(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[CommentDAO] " + e.getMessage());
        }

        return comments;
    }

    public Comment findById(int commentId) {
        String sql = "SELECT * FROM comments WHERE comment_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, commentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToComment(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[CommentDAO] " + e.getMessage());
        }

        return null;
    }

    public boolean delete(int commentId) {
        String sql = "DELETE FROM comments WHERE comment_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, commentId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[CommentDAO] " + e.getMessage());
            return false;
        }
    }

    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setCommentId(rs.getInt("comment_id"));
        comment.setPostId(rs.getString("post_id"));
        comment.setPlayerPid(rs.getString("player_pid"));
        comment.setNickname(rs.getString("nickname"));
        comment.setContent(rs.getString("content"));

        int parentId = rs.getInt("parent_comment_id");
        if (!rs.wasNull()) {
            comment.setParentCommentId(parentId);
        }

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            comment.setCreatedAt(ts.toLocalDateTime());
        }

        return comment;
    }
}
