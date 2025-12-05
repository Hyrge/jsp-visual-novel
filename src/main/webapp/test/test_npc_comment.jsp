<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="util.NPCReactionManager" %>
<%@ page import="util.LLMManager" %>
<%@ page import="util.GeminiService" %>
<%@ page import="manager.NPCUserManager" %>
<%@ page import="model.NPCUser" %>
<%@ page import="model.GameContext" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>NPC 댓글 생성 테스트</title>
    <style>
        body { font-family: monospace; padding: 20px; background: #1e1e1e; color: #d4d4d4; }
        .section { background: #2d2d2d; padding: 15px; margin: 10px 0; border-radius: 8px; }
        .success { color: #4ec9b0; }
        .error { color: #f14c4c; }
        .warning { color: #cca700; }
        h2 { color: #569cd6; border-bottom: 1px solid #569cd6; padding-bottom: 5px; }
        pre { background: #1e1e1e; padding: 10px; overflow-x: auto; }
    </style>
</head>
<body>
    <h1>🔍 NPC 댓글 생성 디버깅</h1>

    <div class="section">
        <h2>1. NPCUserManager 확인</h2>
        <%
            try {
                NPCUserManager npcManager = NPCUserManager.getInstance();
                List<NPCUser> allNPCs = npcManager.getAllNPCUsers();
                out.println("<p class='success'>✅ NPCUserManager 로드 성공</p>");
                out.println("<p>총 NPC 수: <strong>" + allNPCs.size() + "</strong>명</p>");
                
                if (!allNPCs.isEmpty()) {
                    NPCUser sample = allNPCs.get(0);
                    out.println("<p>샘플 NPC: " + sample.getId() + " / " + sample.getTemplateName() + "</p>");
                }
                
                // 현재 시간 온라인 NPC
                String currentTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                List<NPCUser> onlineNPCs = npcManager.getOnlineNPCs(currentTimeStr);
                out.println("<p>현재 시간(" + currentTimeStr + ") 온라인 NPC: <strong>" + onlineNPCs.size() + "</strong>명</p>");
            } catch (Exception e) {
                out.println("<p class='error'>❌ NPCUserManager 오류: " + e.getMessage() + "</p>");
                e.printStackTrace();
            }
        %>
    </div>

    <div class="section">
        <h2>2. LLMManager 확인</h2>
        <%
            try {
                LLMManager llmManager = LLMManager.getInstance();
                List<Map<String, Object>> profiles = llmManager.getAllNPCProfiles();
                
                if (profiles != null && !profiles.isEmpty()) {
                    out.println("<p class='success'>✅ LLMManager 프로필 로드 성공</p>");
                    out.println("<p>프로필 수: <strong>" + profiles.size() + "</strong>개</p>");
                    
                    Map<String, Object> sample = profiles.get(0);
                    out.println("<p>샘플: id=" + sample.get("id") + ", type=" + sample.get("npcType") + "</p>");
                } else {
                    out.println("<p class='error'>❌ LLMManager 프로필이 비어있음!</p>");
                }
            } catch (Exception e) {
                out.println("<p class='error'>❌ LLMManager 오류: " + e.getMessage() + "</p>");
                e.printStackTrace();
            }
        %>
    </div>

    <div class="section">
        <h2>3. GeminiService 확인</h2>
        <%
            try {
                GeminiService gemini = GeminiService.getInstance();
                out.println("<p class='success'>✅ GeminiService 인스턴스 생성 성공</p>");
                out.println("<p>모델: " + gemini.getModel() + "</p>");
                
                // 간단한 테스트 호출
                String testResult = gemini.generateText("안녕이라고만 말해");
                if (testResult != null) {
                    out.println("<p class='success'>✅ API 호출 성공</p>");
                    out.println("<p>응답: " + testResult + "</p>");
                } else {
                    out.println("<p class='error'>❌ API 응답이 null</p>");
                }
            } catch (Exception e) {
                out.println("<p class='error'>❌ GeminiService 오류: " + e.getMessage() + "</p>");
                e.printStackTrace();
            }
        %>
    </div>

    <div class="section">
        <h2>4. NPCReactionManager 큐 상태</h2>
        <%
            try {
                NPCReactionManager reactionManager = NPCReactionManager.getInstance();
                int queueSize = reactionManager.getQueueSize();
                LocalDateTime nextTime = reactionManager.getNextReactionTime();
                
                out.println("<p>현재 큐 크기: <strong>" + queueSize + "</strong></p>");
                if (nextTime != null) {
                    out.println("<p>다음 이벤트 시간: <strong>" + nextTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "</strong></p>");
                } else {
                    out.println("<p class='warning'>⚠️ 예약된 이벤트 없음</p>");
                }
            } catch (Exception e) {
                out.println("<p class='error'>❌ NPCReactionManager 오류: " + e.getMessage() + "</p>");
                e.printStackTrace();
            }
        %>
    </div>

    <div class="section">
        <h2>5. GameContext 세션 확인</h2>
        <%
            GameContext gameContext = (GameContext) session.getAttribute("gameContext");
            if (gameContext != null) {
                out.println("<p class='success'>✅ GameContext 있음</p>");
                out.println("<p>현재 게임 시간: " + gameContext.getGameState().getCurrentDateTime() + "</p>");
                out.println("<p>여론: " + gameContext.getGameState().getReputation() + "</p>");
            } else {
                out.println("<p class='warning'>⚠️ GameContext 없음 (로그인 필요)</p>");
            }
        %>
    </div>

    <div class="section">
        <h2>6. 댓글 생성 테스트</h2>
        <%
            String testAction = request.getParameter("testComment");
            if ("true".equals(testAction)) {
                try {
                    LLMManager llm = LLMManager.getInstance();
                    List<Map<String, Object>> profiles = llm.getAllNPCProfiles();
                    
                    if (profiles != null && !profiles.isEmpty()) {
                        String npcId = (String) profiles.get(0).get("id");
                        out.println("<p>테스트 NPC ID: " + npcId + "</p>");
                        
                        String comment = llm.generateComment(npcId, "테스트 제목", "테스트 내용입니다.", 50);
                        
                        if (comment != null) {
                            out.println("<p class='success'>✅ 댓글 생성 성공!</p>");
                            out.println("<pre>" + comment + "</pre>");
                        } else {
                            out.println("<p class='error'>❌ 댓글 생성 결과가 null</p>");
                        }
                    }
                } catch (Exception e) {
                    out.println("<p class='error'>❌ 댓글 생성 오류: " + e.getMessage() + "</p>");
                    e.printStackTrace();
                }
            } else {
                out.println("<a href='?testComment=true' style='color:#4ec9b0;'>▶ 댓글 생성 테스트 실행</a>");
            }
        %>
    </div>

    <div class="section">
        <h2>7. 이벤트 처리 테스트 (큐에서 꺼내서 실행)</h2>
        <%
            String testProcess = request.getParameter("testProcess");
            if ("true".equals(testProcess)) {
                try {
                    NPCReactionManager reactionManager = NPCReactionManager.getInstance();
                    LocalDateTime nextTime = reactionManager.getNextReactionTime();
                    
                    out.println("<p>처리 전 큐 크기: " + reactionManager.getQueueSize() + "</p>");
                    
                    if (nextTime != null) {
                        out.println("<p>다음 이벤트 시간으로 처리: " + nextTime + "</p>");
                        
                        // 이벤트 처리
                        java.util.List<NPCReactionManager.NPCReactionResult> results = reactionManager.processReactions(nextTime);
                        
                        out.println("<p class='success'>✅ 처리 완료!</p>");
                        out.println("<p>처리된 이벤트 수: " + results.size() + "</p>");
                        out.println("<p>처리 후 큐 크기: " + reactionManager.getQueueSize() + "</p>");
                        
                        // 결과 출력
                        for (NPCReactionManager.NPCReactionResult result : results) {
                            out.println("<div style='background:#1e1e1e;padding:10px;margin:5px 0;border-radius:4px;'>");
                            out.println("<p>NPC ID: " + result.getNpcId() + "</p>");
                            out.println("<p>타입: " + result.getType() + "</p>");
                            out.println("<p>시간: " + result.getExecutedTime() + "</p>");
                            out.println("<p>생성된 텍스트: <strong>" + result.getGeneratedText() + "</strong></p>");
                            out.println("</div>");
                        }
                        
                        if (results.isEmpty()) {
                            out.println("<p class='warning'>⚠️ 처리된 결과가 없음 (시간 조건 미충족?)</p>");
                        }
                    } else {
                        out.println("<p class='warning'>⚠️ 큐에 이벤트가 없음</p>");
                    }
                } catch (Exception e) {
                    out.println("<p class='error'>❌ 처리 오류: " + e.getMessage() + "</p>");
                    out.println("<pre>");
                    e.printStackTrace(new java.io.PrintWriter(out));
                    out.println("</pre>");
                }
            } else {
                out.println("<a href='?testProcess=true' style='color:#4ec9b0;'>▶ 이벤트 처리 테스트 실행</a>");
            }
        %>
    </div>

    <div class="section">
        <h2>8. 반응 예약 테스트</h2>
        <%
            String testSchedule = request.getParameter("testSchedule");
            if ("true".equals(testSchedule)) {
                try {
                    NPCReactionManager reactionManager = NPCReactionManager.getInstance();
                    LocalDateTime now = LocalDateTime.now();
                    
                    out.println("<p>현재 시간: " + now + "</p>");
                    out.println("<p>예약 전 큐 크기: " + reactionManager.getQueueSize() + "</p>");
                    
                    reactionManager.scheduleCommentReactions(
                        "TEST_POST_001",
                        "테스트 게시글",
                        "이것은 테스트 내용입니다.",
                        now,
                        50
                    );
                    
                    out.println("<p class='success'>✅ 예약 완료!</p>");
                    out.println("<p>예약 후 큐 크기: " + reactionManager.getQueueSize() + "</p>");
                    
                    LocalDateTime nextTime = reactionManager.getNextReactionTime();
                    if (nextTime != null) {
                        out.println("<p>다음 이벤트: " + nextTime + "</p>");
                    }
                } catch (Exception e) {
                    out.println("<p class='error'>❌ 예약 오류: " + e.getMessage() + "</p>");
                    e.printStackTrace();
                }
            } else {
                out.println("<a href='?testSchedule=true' style='color:#4ec9b0;'>▶ 반응 예약 테스트 실행</a>");
            }
        %>
    </div>

</body>
</html>

