<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.GameContext" %>

<%
    request.setCharacterEncoding("UTF-8");
    
    // 세션에서 GameContext 가져오기
    GameContext gameContext = (GameContext) session.getAttribute("gameContext");
    
    if (gameContext == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    
    String questId = request.getParameter("questId");
    
    if (questId == null || questId.trim().isEmpty()) {
        out.println("<script>alert('퀘스트 ID가 없습니다.'); history.back();</script>");
        return;
    }
    
    // 퀘스트 완료 처리
    gameContext.getQuestService().completeQuest(questId);
    
    // 이전 페이지로 리다이렉트 (referrer 또는 기본 페이지)
    String referer = request.getHeader("Referer");
    if (referer != null && !referer.isEmpty()) {
        response.sendRedirect(referer);
    } else {
        response.sendRedirect(request.getContextPath() + "/views/board/kdolTalkBoard.jsp");
    }
%>
