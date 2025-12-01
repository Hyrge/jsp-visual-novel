<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="util.GeminiService, util.LLMManager, util.NPCReactionManager" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.time.LocalDateTime" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>LLM 기능 테스트</title>
    <style>
        body {
            font-family: 'Malgun Gothic', sans-serif;
            max-width: 1200px;
            margin: 20px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .test-section {
            background: white;
            padding: 20px;
            margin-bottom: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        h2 {
            color: #333;
            border-bottom: 2px solid #4CAF50;
            padding-bottom: 10px;
        }
        .result {
            background: #f9f9f9;
            padding: 15px;
            margin-top: 10px;
            border-left: 4px solid #4CAF50;
            white-space: pre-wrap;
        }
        .error {
            background: #ffebee;
            color: #c62828;
            padding: 15px;
            margin-top: 10px;
            border-left: 4px solid #c62828;
        }
        button {
            background: #4CAF50;
            color: white;
            border: none;
            padding: 10px 20px;
            font-size: 14px;
            cursor: pointer;
            border-radius: 4px;
            margin: 5px;
        }
        button:hover {
            background: #45a049;
        }
        textarea {
            width: 100%;
            min-height: 100px;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-family: inherit;
        }
        input[type="text"] {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        label {
            display: block;
            margin-top: 10px;
            font-weight: bold;
            color: #555;
        }
    </style>
</head>
<body>
    <h1>🤖 LLM 기능 테스트 페이지</h1>

    <%
    String action = request.getParameter("action");
    String resultMessage = null;
    String errorMessage = null;

    if ("testGemini".equals(action)) {
        // Gemini API 기본 테스트
        try {
            GeminiService geminiService = GeminiService.getInstance();
            String testPrompt = "안녕하세요! 한국어로 짧게 인사해주세요.";
            String apiResponse = geminiService.generateText(testPrompt);
            resultMessage = "Gemini API 응답:\n" + apiResponse;
        } catch (Exception e) {
            errorMessage = "Gemini API 테스트 실패: " + e.getMessage();
            e.printStackTrace();
        }
    } else if ("testComment".equals(action)) {
        // NPC 댓글 생성 테스트
        try {
            String npcId = request.getParameter("npcId");
            String postTitle = request.getParameter("postTitle");
            String postContent = request.getParameter("postContent");
            int sentiment = Integer.parseInt(request.getParameter("sentiment"));

            LLMManager llmManager = LLMManager.getInstance();
            String comment = llmManager.generateComment(npcId, postTitle, postContent, sentiment);

            if (comment != null) {
                resultMessage = "생성된 댓글:\n" + comment;
            } else {
                errorMessage = "댓글 생성 실패";
            }
        } catch (Exception e) {
            errorMessage = "댓글 생성 중 오류: " + e.getMessage();
            e.printStackTrace();
        }
    } else if ("testPost".equals(action)) {
        // NPC 게시글 생성 테스트
        try {
            String npcId = request.getParameter("npcId");
            String topic = request.getParameter("topic");
            int sentiment = Integer.parseInt(request.getParameter("sentiment"));

            LLMManager llmManager = LLMManager.getInstance();
            Map<String, String> post = llmManager.generatePost(npcId, topic, sentiment);

            if (post != null) {
                resultMessage = "생성된 게시글:\n제목: " + post.get("title") + "\n\n본문:\n" + post.get("content");
            } else {
                errorMessage = "게시글 생성 실패";
            }
        } catch (Exception e) {
            errorMessage = "게시글 생성 중 오류: " + e.getMessage();
            e.printStackTrace();
        }
    } else if ("testReactionQueue".equals(action)) {
        // 반응 큐 테스트
        try {
            NPCReactionManager reactionManager = NPCReactionManager.getInstance();
            LocalDateTime now = LocalDateTime.now();

            // 테스트 댓글 반응 예약
            reactionManager.scheduleCommentReactions(
                "test_post_1",
                "MiNa 신곡 어때요?",
                "민아 신곡 진짜 좋은 것 같아요! 다들 어떻게 생각하세요?",
                now,
                50
            );

            int queueSize = reactionManager.getQueueSize();
            LocalDateTime nextReaction = reactionManager.getNextReactionTime();

            resultMessage = "반응 큐 테스트 완료\n큐 크기: " + queueSize + "\n다음 반응 예정 시각: " + nextReaction;
        } catch (Exception e) {
            errorMessage = "반응 큐 테스트 실패: " + e.getMessage();
            e.printStackTrace();
        }
    }
    %>

    <!-- 1. Gemini API 기본 테스트 -->
    <div class="test-section">
        <h2>1. Gemini API 기본 테스트</h2>
        <p>Gemini API 연결이 정상적으로 작동하는지 확인합니다.</p>
        <form method="post">
            <input type="hidden" name="action" value="testGemini">
            <button type="submit">Gemini API 테스트</button>
        </form>
    </div>

    <!-- 2. NPC 댓글 생성 테스트 -->
    <div class="test-section">
        <h2>2. NPC 댓글 생성 테스트</h2>
        <form method="post">
            <input type="hidden" name="action" value="testComment">

            <label>NPC ID:</label>
            <input type="text" name="npcId" value="4f91ac" placeholder="예: 4f91ac (여고생 A)">

            <label>게시글 제목:</label>
            <input type="text" name="postTitle" value="MiNa 신곡 대박이네요" placeholder="게시글 제목">

            <label>게시글 내용:</label>
            <textarea name="postContent" placeholder="게시글 내용">방금 민아 신곡 들었는데 진짜 좋은 것 같아요! 멜로디도 중독성 있고 가사도 좋더라구요. 다들 들어보셨나요?</textarea>

            <label>현재 여론 (0~100):</label>
            <input type="text" name="sentiment" value="50" placeholder="0~100">

            <button type="submit">댓글 생성</button>
        </form>
    </div>

    <!-- 3. NPC 게시글 생성 테스트 -->
    <div class="test-section">
        <h2>3. NPC 게시글 생성 테스트</h2>
        <form method="post">
            <input type="hidden" name="action" value="testPost">

            <label>NPC ID:</label>
            <input type="text" name="npcId" value="3bc9d0" placeholder="예: 3bc9d0 (무직 A - 안티)">

            <label>주제:</label>
            <textarea name="topic" placeholder="게시글 주제">MiNa가 인스타에서 팬들에게 감사 인사를 올렸다는 소식</textarea>

            <label>현재 여론 (0~100):</label>
            <input type="text" name="sentiment" value="50" placeholder="0~100">

            <button type="submit">게시글 생성</button>
        </form>
    </div>

    <!-- 4. 반응 큐 시스템 테스트 -->
    <div class="test-section">
        <h2>4. NPC 반응 큐 시스템 테스트</h2>
        <p>플레이어 게시글에 대한 NPC 반응이 시간차로 예약되는지 확인합니다.</p>
        <form method="post">
            <input type="hidden" name="action" value="testReactionQueue">
            <button type="submit">반응 큐 테스트</button>
        </form>
    </div>

    <!-- 결과 표시 -->
    <% if (resultMessage != null) { %>
        <div class="test-section">
            <h2>✅ 테스트 결과</h2>
            <div class="result"><%= resultMessage %></div>
        </div>
    <% } %>

    <% if (errorMessage != null) { %>
        <div class="test-section">
            <h2>❌ 오류 발생</h2>
            <div class="error"><%= errorMessage %></div>
        </div>
    <% } %>

    <!-- NPC 프로필 목록 -->
    <div class="test-section">
        <h2>📋 사용 가능한 NPC 목록</h2>
        <%
        try {
            LLMManager llmManager = LLMManager.getInstance();
            java.util.List<Map<String, Object>> profiles = llmManager.getAllNPCProfiles();
            if (profiles != null) {
                out.println("<table style='width:100%; border-collapse: collapse;'>");
                out.println("<tr style='background:#f0f0f0;'><th style='padding:8px; border:1px solid #ddd;'>ID</th><th style='padding:8px; border:1px solid #ddd;'>이름</th><th style='padding:8px; border:1px solid #ddd;'>유형</th><th style='padding:8px; border:1px solid #ddd;'>성향</th><th style='padding:8px; border:1px solid #ddd;'>말투</th></tr>");
                for (Map<String, Object> profile : profiles) {
                    out.println("<tr>");
                    out.println("<td style='padding:8px; border:1px solid #ddd;'>" + profile.get("id") + "</td>");
                    out.println("<td style='padding:8px; border:1px solid #ddd;'>" + profile.get("templateName") + "</td>");
                    out.println("<td style='padding:8px; border:1px solid #ddd;'>" + profile.get("npcType") + "</td>");
                    out.println("<td style='padding:8px; border:1px solid #ddd;'>" + profile.get("baseSentiment") + "</td>");
                    out.println("<td style='padding:8px; border:1px solid #ddd;'>" + profile.get("speechStyle") + "</td>");
                    out.println("</tr>");
                }
                out.println("</table>");
            }
        } catch (Exception e) {
            out.println("<div class='error'>NPC 프로필 로드 실패: " + e.getMessage() + "</div>");
        }
        %>
    </div>

    <div class="test-section">
        <h2>⚙️ 설정 안내</h2>
        <p><strong>중요:</strong> Gemini API 키를 설정해야 합니다.</p>
        <p>경로: <code>WEB-INF/gemini.properties</code></p>
        <p>현재 설정된 API 키가 유효한지 확인하세요.</p>
        <hr style="margin: 15px 0;">
        <p><strong>사용 중인 SDK:</strong> Google Gen AI Java SDK 1.28.0 (공식 SDK)</p>
        <p><strong>기본 모델:</strong> gemini-2.5-flash-exp</p>
    </div>

    <a href="index.jsp" style="display:inline-block; margin-top:20px; color:#4CAF50;">← 메인으로 돌아가기</a>
</body>
</html>
