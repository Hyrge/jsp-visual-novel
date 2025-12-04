package manager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dto.Comment;
import dto.Post;
import util.JsonLoader;

public class DataManager {
    private static DataManager instance;
    private List<Map<String, Object>> eventConfig;
    private List<Map<String, Object>> questConfig;
    private Map<String, Post> initialPosts;
    private Map<String, List<Comment>> initialComments;

    private DataManager() {
        this.initialPosts = new HashMap<>();
        this.initialComments = new HashMap<>();
        loadAll();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private void loadAll() {
        System.out.println("DataManager: Loading data");

        try {
            // 이벤트 설정 로드 (classpath에서 로드)
            String eventPath = getClass().getClassLoader().getResource("event_config.json").getPath();
            eventPath = java.net.URLDecoder.decode(eventPath, "UTF-8");
            this.eventConfig = JsonLoader.loadJSON(eventPath);

            if (this.eventConfig != null) {
                System.out.println("DataManager: Loaded " + this.eventConfig.size() + " events.");
            } else {
                System.err.println("DataManager: Failed to load event_config.json");
            }
        } catch (Exception e) {
            System.err.println("DataManager: Error loading event_config.json - " + e.getMessage());
            e.printStackTrace();
        }

        try {
            // 퀘스트 설정 로드 (classpath에서 로드)
            String questPath = getClass().getClassLoader().getResource("quest_config.json").getPath();
            questPath = java.net.URLDecoder.decode(questPath, "UTF-8");
            this.questConfig = JsonLoader.loadJSON(questPath);

            if (this.questConfig != null) {
                System.out.println("DataManager: Loaded " + this.questConfig.size() + " quests.");
            } else {
                System.err.println("DataManager: Failed to load quest_config.json");
            }
        } catch (Exception e) {
            System.err.println("DataManager: Error loading quest_config.json - " + e.getMessage());
            e.printStackTrace();
        }

        // 게시글/댓글 초기 데이터 로드
        loadPostData();
    }

    private void loadPostData() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        try {
            // post.json 로드
            InputStream postStream = getClass().getClassLoader().getResourceAsStream("post.json");
            if (postStream != null) {
                List<Post> posts = mapper.readValue(postStream, new TypeReference<List<Post>>() {});
                posts.forEach(p -> initialPosts.put(p.getPostId(), p));
                System.out.println("DataManager: Loaded " + posts.size() + " posts from post.json");
            } else {
                System.err.println("DataManager: post.json not found!");
            }

            // mina_post.json 로드
            InputStream minaStream = getClass().getClassLoader().getResourceAsStream("mina_post.json");
            if (minaStream != null) {
                List<Post> minaPosts = mapper.readValue(minaStream, new TypeReference<List<Post>>() {});
                minaPosts.forEach(p -> initialPosts.put(p.getPostId(), p));
                System.out.println("DataManager: Loaded " + minaPosts.size() + " posts from mina_post.json");
            } else {
                System.err.println("DataManager: mina_post.json not found!");
            }

            // comment.json 로드
            InputStream commentStream = getClass().getClassLoader().getResourceAsStream("comment.json");
            if (commentStream != null) {
                List<Comment> comments = mapper.readValue(commentStream, new TypeReference<List<Comment>>() {});
                for (Comment c : comments) {
                    initialComments.computeIfAbsent(c.getPostId(), k -> new ArrayList<>()).add(c);
                }
                System.out.println("DataManager: Loaded " + comments.size() + " comments from comment.json");
            } else {
                System.err.println("DataManager: comment.json not found!");
            }

            System.out.println("DataManager: Total posts loaded: " + initialPosts.size());

        } catch (Exception e) {
            System.err.println("DataManager: Error loading post data - " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> getEventConfig() {
        return eventConfig;
    }

    public List<Map<String, Object>> getQuestConfig() {
        return questConfig;
    }

    public Map<String, Post> getInitialPosts() {
        return initialPosts;
    }

    public Map<String, List<Comment>> getInitialComments() {
        return initialComments;
    }
}
