package dao;

import dto.Post;
import util.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    public boolean insert(Post post) {
        String sql = "INSERT INTO posts (post_id, author_pid, title, content, board_type, category, " +
                     "created_at, has_pictures, like_count, dislike_count, is_related_mina) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, post.getPostId());
            pstmt.setString(2, post.getAuthorPid());
            pstmt.setString(3, post.getTitle());
            pstmt.setString(4, post.getContent());
            pstmt.setString(5, post.getBoardType());
            pstmt.setString(6, post.getCategory());
            pstmt.setTimestamp(7, Timestamp.valueOf(post.getCreatedAt()));
            pstmt.setBoolean(8, post.isHasPictures());
            pstmt.setInt(9, post.getLikeCount());
            pstmt.setInt(10, post.getDislikeCount());
            pstmt.setBoolean(11, post.isRelatedMina());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Post findById(String postId) {
        String sql = "SELECT * FROM posts WHERE post_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, postId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPost(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Post> findByBoardType(String boardType) {
        String sql = "SELECT * FROM posts WHERE board_type = ? ORDER BY created_at DESC";
        List<Post> posts = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, boardType);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapResultSetToPost(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return posts;
    }

    public boolean update(Post post) {
        String sql = "UPDATE posts SET title = ?, content = ?, category = ?, has_pictures = ?, " +
                     "like_count = ?, dislike_count = ? WHERE post_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, post.getTitle());
            pstmt.setString(2, post.getContent());
            pstmt.setString(3, post.getCategory());
            pstmt.setBoolean(4, post.isHasPictures());
            pstmt.setInt(5, post.getLikeCount());
            pstmt.setInt(6, post.getDislikeCount());
            pstmt.setString(7, post.getPostId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String postId) {
        String sql = "DELETE FROM posts WHERE post_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, postId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Post mapResultSetToPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setPostId(rs.getString("post_id"));
        post.setAuthorPid(rs.getString("author_pid"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        post.setBoardType(rs.getString("board_type"));
        post.setCategory(rs.getString("category"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            post.setCreatedAt(ts.toLocalDateTime());
        }

        post.setHasPictures(rs.getBoolean("has_pictures"));
        post.setLikeCount(rs.getInt("like_count"));
        post.setDislikeCount(rs.getInt("dislike_count"));
        post.setRelatedMina(rs.getBoolean("is_related_mina"));

        return post;
    }
}
