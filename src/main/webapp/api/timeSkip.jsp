<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.GameContext" %>
<%@ page import="model.GameState" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    
    // 쿠키에서 pid 가져오기
    String pid = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("pid".equals(cookie.getName())) {
                pid = cookie.getValue();
                break;
            }
        }
    }
    
    if (pid == null) {
        out.print("{\"success\": false, \"error\": \"로그인이 필요합니다.\"}");
        return;
    }
    
    // GameContext 가져오기
    GameContext gameContext = (GameContext) session.getAttribute("gameContext");
    if (gameContext == null) {
        out.print("{\"success\": false, \"error\": \"게임 세션이 없습니다.\"}");
        return;
    }
    
    GameState gameState = gameContext.getGameState();
    String type = request.getParameter("type");
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    if ("event".equals(type)) {
        // 다음 이벤트로 스킵
        LocalDateTime nextEventTime = gameState.getNextEventTime();
        
        if (nextEventTime == null) {
            out.print("{\"success\": false, \"error\": \"예약된 이벤트가 없습니다.\"}");
            return;
        }
        
        LocalDateTime beforeTime = gameState.getCurrentDateTime();
        int processedCount = gameState.advanceToNextEvent(pid);
        LocalDateTime afterTime = gameState.getCurrentDateTime();
        
        out.print("{\"success\": true, ");
        out.print("\"beforeTime\": \"" + beforeTime.format(formatter) + "\", ");
        out.print("\"afterTime\": \"" + afterTime.format(formatter) + "\", ");
        out.print("\"processedEvents\": " + processedCount + ", ");
        out.print("\"message\": \"" + processedCount + "개의 이벤트가 처리되었습니다.\"}");
        
    } else if ("day".equals(type)) {
        // 다음날로 스킵
        LocalDateTime beforeTime = gameState.getCurrentDateTime();
        
        // 다음날 09:00으로 설정
        gameState.setCurrentDate(gameState.getCurrentDate().plusDays(1));
        gameState.setCurrentTime(java.time.LocalTime.of(9, 0));
        
        LocalDateTime afterTime = gameState.getCurrentDateTime();
        
        // 그 사이 이벤트들 처리
        util.NPCReactionManager reactionManager = util.NPCReactionManager.getInstance();
        java.util.List<util.NPCReactionManager.NPCReactionResult> results = reactionManager.processReactions(afterTime);
        
        // 댓글 저장
        service.PostService postService = gameContext.getPostService();
        int savedCount = 0;
        for (util.NPCReactionManager.NPCReactionResult result : results) {
            if (result.getType() == util.NPCReactionManager.NPCReactionType.COMMENT && result.getGeneratedText() != null) {
                try {
                    String postId = (String) result.getOriginalParameters().get("postId");
                    dto.Comment comment = new dto.Comment();
                    comment.setPostId(postId);
                    comment.setPlayerPid(pid);
                    comment.setContent(result.getGeneratedText());
                    comment.setCreatedAt(result.getExecutedTime());
                    String npcNickname = postService.assignNicknameForNPC(result.getNpcId(), postId);
                    comment.setAuthorNickname(npcNickname);
                    if (postService.createComment(comment)) {
                        savedCount++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        out.print("{\"success\": true, ");
        out.print("\"beforeTime\": \"" + beforeTime.format(formatter) + "\", ");
        out.print("\"afterTime\": \"" + afterTime.format(formatter) + "\", ");
        out.print("\"processedEvents\": " + savedCount + ", ");
        out.print("\"message\": \"다음날로 이동했습니다. " + savedCount + "개의 이벤트가 처리되었습니다.\"}");
        
    } else {
        out.print("{\"success\": false, \"error\": \"잘못된 요청입니다.\"}");
    }
%>

