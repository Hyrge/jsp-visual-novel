package dao;

import dto.Comment;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    public boolean insert(Comment comment) {
        String sql = "INSERT INTO comments (comment_seq, post_id, author_pid, content, " +
                     "parent_comment_id, created_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, comment.getCommentSeq());
            pstmt.setString(2, comment.getPostId());
            pstmt.setString(3, comment.getAuthorPid());
            pstmt.setString(4, comment.getContent());

            if (comment.getParentCommentId() != null) {
                pstmt.setInt(5, comment.getParentCommentId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            pstmt.setTimestamp(6, Timestamp.valueOf(comment.getCreatedAt()));

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        comment.setCommentId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Comment> findByPostId(String postId) {
        return findByPostId(postId, null);
    }

    public List<Comment> findByPostId(String postId, String playerPid) {
        String sql;
        if (playerPid != null) {
            sql = "SELECT * FROM comments WHERE post_id = ? AND author_pid = ? ORDER BY comment_seq ASC, created_at ASC";
        } else {
            sql = "SELECT * FROM comments WHERE post_id = ? ORDER BY comment_seq ASC, created_at ASC";
        }
        List<Comment> comments = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, postId);
            if (playerPid != null) {
                pstmt.setString(2, playerPid);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapResultSetToComment(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
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
        }

        return null;
    }

    public int getNextCommentSeq(String postId) {
        String sql = "SELECT COALESCE(MAX(comment_seq), 0) + 1 FROM comments WHERE post_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, postId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 1;
    }

    public boolean delete(int commentId) {
        String sql = "DELETE FROM comments WHERE comment_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, commentId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setCommentId(rs.getInt("comment_id"));
        comment.setCommentSeq(rs.getInt("comment_seq"));
        comment.setPostId(rs.getString("post_id"));
        comment.setAuthorPid(rs.getString("author_pid"));
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
