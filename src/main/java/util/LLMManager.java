package util;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * NPC의 댓글/게시글 생성을 관리하는 LLM 매니저
 * GeminiService를 활용하여 NPC 프로필에 맞는 텍스트 생성
 */
public class LLMManager {
    private static LLMManager instance;
    private GeminiService geminiService;
    private List<Map<String, Object>> npcProfiles;
    private Random random;

    private LLMManager() {
        this.geminiService = GeminiService.getInstance();
        this.random = new Random();
        loadNPCProfiles();
    }

    public static LLMManager getInstance() {
        if (instance == null) {
            synchronized (LLMManager.class) {
                if (instance == null) {
                    instance = new LLMManager();
                }
            }
        }
        return instance;
    }

    /**
     * NPC 프로필 데이터 로드
     */
    private void loadNPCProfiles() {
        try {
            // Maven 프로젝트에서는 src/main/resources/에서 직접 로드
            String profilePath = getClass().getClassLoader().getResource("profile_config.json").getPath();
            // URL 디코딩 처리 (공백 등 특수문자 처리)
            profilePath = java.net.URLDecoder.decode(profilePath, "UTF-8");
            this.npcProfiles = JsonLoader.loadJSON(profilePath);
            System.out.println("LLMManager: NPC 프로필 " + npcProfiles.size() + "개 로드 완료");
        } catch (Exception e) {
            System.err.println("LLMManager: NPC 프로필 로드 실패 - " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ID로 NPC 프로필 조회
     */
    public Map<String, Object> getNPCProfile(String npcId) {
        if (npcProfiles == null) {
			return null;
		}

        for (Map<String, Object> profile : npcProfiles) {
            if (profile.get("id").equals(npcId)) {
                return profile;
            }
        }
        return null;
    }

    /**
     * NPC 댓글 생성
     * @param npcId NPC 프로필 ID
     * @param postTitle 게시글 제목
     * @param postContent 게시글 내용
     * @param currentSentiment 현재 여론 (0~100)
     * @return 생성된 댓글 텍스트
     */
    public String generateComment(String npcId, String postTitle, String postContent, int currentSentiment) {
        return generateComment(npcId, postTitle, postContent, currentSentiment, false);
    }

    /**
     * NPC 댓글 생성 (MiNa 관련 여부 지정)
     * @param npcId NPC 프로필 ID
     * @param postTitle 게시글 제목
     * @param postContent 게시글 내용
     * @param currentSentiment 현재 여론 (0~100)
     * @param isRelatedMina MiNa 관련 글인지 여부
     * @return 생성된 댓글 텍스트
     */
    public String generateComment(String npcId, String postTitle, String postContent, int currentSentiment, boolean isRelatedMina) {
        Map<String, Object> profile = getNPCProfile(npcId);
        if (profile == null) {
            System.err.println("LLMManager: NPC 프로필을 찾을 수 없음 - " + npcId);
            return null;
        }

        // 미나 관련 글이면 미나 프롬프트, 아니면 일반 프롬프트
        String prompt;
        if (isRelatedMina) {
            prompt = buildMinaCommentPrompt(profile, postTitle, postContent, currentSentiment);
        } else {
            prompt = buildCommentPrompt(profile, postTitle, postContent, currentSentiment);
        }

        // Gemini API 호출
        String generatedText = geminiService.generateText(prompt);

        if (generatedText != null) {
            // 후처리: 따옴표 제거, 공백 정리 등
            generatedText = postProcessText(generatedText);
        }

        return generatedText;
    }

    /**
     * NPC 게시글 생성
     * @param npcId NPC 프로필 ID
     * @param topic 주제 (이슈 내용)
     * @param currentSentiment 현재 여론 (0~100)
     * @return Map with "title" and "content"
     */
    public Map<String, String> generatePost(String npcId, String topic, int currentSentiment) {
        Map<String, Object> profile = getNPCProfile(npcId);
        if (profile == null) {
            System.err.println("LLMManager: NPC 프로필을 찾을 수 없음 - " + npcId);
            return null;
        }

        // 제목 생성 프롬프트
        String titlePrompt = buildPostTitlePrompt(profile, topic, currentSentiment);
        String title = geminiService.generateText(titlePrompt);

        if (title == null) {
			return null;
		}
        title = postProcessText(title);

        // 본문 생성 프롬프트
        String contentPrompt = buildPostContentPrompt(profile, topic, title, currentSentiment);
        String content = geminiService.generateText(contentPrompt);

        if (content == null) {
			return null;
		}
        content = postProcessText(content);

        return Map.of("title", title, "content", content);
    }

    /**
     * 일반 댓글 생성 프롬프트 (미나 관련 아닐 때)
     */
    private String buildCommentPrompt(Map<String, Object> profile, String postTitle, String postContent, int sentiment) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("당신은 한국 아이돌 팬 커뮤니티 '더꾸'의 회원입니다.\n\n");
        prompt.append("### 당신의 프로필:\n");
        prompt.append("- 유형: ").append(profile.get("npcType")).append("\n");
        prompt.append("- 말투: ").append(profile.get("speechStyle")).append("\n");
        prompt.append("- 성격: ").append(profile.get("personalityDesc")).append("\n\n");

        prompt.append("### 게시글:\n");
        prompt.append("제목: ").append(postTitle).append("\n");
        prompt.append("내용: ").append(postContent).append("\n\n");

        prompt.append("### 규칙:\n");
        prompt.append("1. 게시글 내용에 맞는 자연스러운 댓글을 작성하세요\n");
        prompt.append("2. 당신의 말투와 성격을 반영하세요\n");
        prompt.append("3. 1~2문장으로 짧게 작성하세요 (50자 이내)\n");
        prompt.append("4. 따옴표나 설명 없이 댓글만 출력하세요\n");
        prompt.append("5. 게시글과 관련 없는 내용은 언급하지 마세요\n\n");

        prompt.append("댓글:");

        return prompt.toString();
    }

    /**
     * MiNa 관련 댓글 생성 프롬프트 (미나 관련 글일 때)
     */
    private String buildMinaCommentPrompt(Map<String, Object> profile, String postTitle, String postContent, int sentiment) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("당신은 커뮤니티 사이트 '더꾸'의 회원입니다.\n\n");
        prompt.append("### 당신의 프로필:\n");
        prompt.append("- 유형: ").append(profile.get("npcType")).append("\n");
        prompt.append("- 설명: ").append(profile.get("description")).append("\n");
        prompt.append("- 말투: ").append(profile.get("speechStyle")).append("\n");
        prompt.append("- 성격: ").append(profile.get("personalityDesc")).append("\n");
        prompt.append("- MiNa(노민아)에 대한 감정: ");

        int baseSentiment = ((Number) profile.get("baseSentiment")).intValue();
        if (baseSentiment >= 5) {
            prompt.append("매우 긍정적 (열성팬)");
        } else if (baseSentiment >= 2) {
            prompt.append("긍정적 (일반팬)");
        } else if (baseSentiment >= -1) {
            prompt.append("중립적");
        } else if (baseSentiment >= -4) {
            prompt.append("부정적");
        } else {
            prompt.append("매우 부정적 (안티)");
        }
        prompt.append("\n\n");

        prompt.append("### 현재 상황:\n");
        prompt.append("- 게시글 제목: ").append(postTitle).append("\n");
        prompt.append("- 게시글 내용: ").append(postContent).append("\n");
        prompt.append("- 현재 MiNa 여론: ").append(sentiment).append("%\n\n");

        prompt.append("### 지침:\n");
        prompt.append("1. 위 게시글에 대한 댓글을 작성하세요\n");
        prompt.append("2. 반드시 당신의 말투와 성격을 반영하세요\n");
        prompt.append("3. MiNa에 대한 당신의 감정을 자연스럽게 표현하세요\n");
        prompt.append("4. 댓글은 1~3문장 정도로 짧게 작성하세요\n");
        prompt.append("5. 따옴표(\"\")나 부가 설명 없이 댓글 내용만 작성하세요\n\n");

        prompt.append("댓글:");

        return prompt.toString();
    }

    /**
     * 게시글 제목 생성 프롬프트 작성
     */
    private String buildPostTitlePrompt(Map<String, Object> profile, String topic, int sentiment) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("당신은 커뮤니티 사이트 '더꾸'의 회원입니다.\n\n");
        prompt.append("### 당신의 프로필:\n");
        prompt.append("- 유형: ").append(profile.get("npcType")).append("\n");
        prompt.append("- 성격: ").append(profile.get("personalityDesc")).append("\n");

        int baseSentiment = ((Number) profile.get("baseSentiment")).intValue();
        prompt.append("- MiNa에 대한 감정: ");
        if (baseSentiment >= 5) {
            prompt.append("매우 긍정적");
        } else if (baseSentiment >= 2) {
            prompt.append("긍정적");
        } else if (baseSentiment >= -1) {
            prompt.append("중립적");
        } else {
            prompt.append("부정적");
        }
        prompt.append("\n\n");

        prompt.append("### 작성할 주제:\n");
        prompt.append(topic).append("\n\n");

        prompt.append("### 지침:\n");
        prompt.append("1. 위 주제로 게시글 제목을 작성하세요\n");
        prompt.append("2. 제목은 10~30자 이내로 작성하세요\n");
        prompt.append("3. 당신의 성향을 반영하되 자연스럽게 작성하세요\n");
        prompt.append("4. 따옴표(\"\")나 부가 설명 없이 제목만 작성하세요\n\n");

        prompt.append("제목:");

        return prompt.toString();
    }

    /**
     * 게시글 본문 생성 프롬프트 작성
     */
    private String buildPostContentPrompt(Map<String, Object> profile, String topic, String title, int sentiment) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("당신은 커뮤니티 사이트 '더꾸'의 회원입니다.\n\n");
        prompt.append("### 당신의 프로필:\n");
        prompt.append("- 유형: ").append(profile.get("npcType")).append("\n");
        prompt.append("- 말투: ").append(profile.get("speechStyle")).append("\n");
        prompt.append("- 성격: ").append(profile.get("personalityDesc")).append("\n\n");

        prompt.append("### 작성할 내용:\n");
        prompt.append("- 주제: ").append(topic).append("\n");
        prompt.append("- 제목: ").append(title).append("\n\n");

        prompt.append("### 지침:\n");
        prompt.append("1. 위 제목에 맞는 게시글 본문을 작성하세요\n");
        prompt.append("2. 3~5문장 정도로 작성하세요\n");
        prompt.append("3. 반드시 당신의 말투를 사용하세요\n");
        prompt.append("4. 따옴표(\"\")나 부가 설명 없이 본문만 작성하세요\n\n");

        prompt.append("본문:");

        return prompt.toString();
    }

    /**
     * 생성된 텍스트 후처리
     */
    private String postProcessText(String text) {
        if (text == null) {
			return null;
		}

        // 앞뒤 공백 제거
        text = text.trim();

        // 따옴표 제거
        if (text.startsWith("\"") && text.endsWith("\"")) {
            text = text.substring(1, text.length() - 1);
        }

        // 불필요한 줄바꿈 정리
        text = text.replaceAll("\n\n+", "\n");

        return text;
    }

    /**
     * 랜덤 NPC 프로필 가져오기
     */
    public Map<String, Object> getRandomNPCProfile() {
        if (npcProfiles == null || npcProfiles.isEmpty()) {
			return null;
		}
        return npcProfiles.get(random.nextInt(npcProfiles.size()));
    }

    /**
     * 전체 NPC 프로필 목록 조회
     */
    public List<Map<String, Object>> getAllNPCProfiles() {
        return npcProfiles;
    }

    /**
     * 게시글이 MiNa(노민아)에 관한 것인지 LLM으로 판단
     * @param title 게시글 제목
     * @param content 게시글 내용
     * @return MiNa 관련 여부 (true/false)
     */
    public boolean isRelatedToMina(String title, String content) {
        String prompt = buildMinaDetectionPrompt(title, content);
        String result = geminiService.generateText(prompt);

        if (result == null) {
            // LLM 호출 실패시 폴백: 기본 키워드 매칭
            return fallbackMinaDetection(title, content);
        }

        // 결과 파싱 (true/false 판단)
        result = result.trim().toLowerCase();
        return result.contains("true") || result.contains("yes") || result.contains("예") || result.contains("관련있");
    }

    /**
     * MiNa 관련 여부 판단 프롬프트 작성
     */
    private String buildMinaDetectionPrompt(String title, String content) {
        return String.join("\n",
            "당신은 텍스트 분석 전문가입니다.",
            "",
            "### 판단 대상 인물:",
            "아이돌 'MiNa' (노민아)를 지칭하는 다양한 표현들:",
            "- 공식명: MiNa, 미나, Mina",
            "- 본명: 노민아",
            "- 별명/애칭: 노미남, 밍토끼, 민아, 송민아",
            "- 기타 변형: 미나야, 민아야, 밍밍이 등",
            "",
            "### 분석할 게시글:",
            "- 제목: " + (title != null ? title : "(없음)"),
            "- 내용: " + (content != null ? content : "(없음)"),
            "",
            "### 지침:",
            "1. 위 게시글이 MiNa(노민아)에 대해 언급하거나 관련된 내용인지 판단하세요",
            "2. 직접적인 언급뿐 아니라 문맥상 MiNa를 지칭하는 경우도 포함합니다",
            "3. 반드시 'true' 또는 'false'로만 답하세요",
            "",
            "답변:"
        );
    }

    /**
     * LLM 호출 실패시 폴백 키워드 매칭
     */
    private boolean fallbackMinaDetection(String title, String content) {
        String combined = ((title != null ? title : "") + " " + (content != null ? content : "")).toLowerCase();
        return combined.contains("mina") || combined.contains("미나") ||
               combined.contains("민아") || combined.contains("노민아") ||
               combined.contains("송민아") || combined.contains("노미남") ||
               combined.contains("밍토끼");
    }
}
