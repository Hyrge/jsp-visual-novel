<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dao.PlayerDAO" %>
<%@ page import="model.GameContext" %>
<%@ page import="manager.GameController" %>
<%
    GameController gameController = GameController.getInstance();
    GameContext gameContext = (GameContext) session.getAttribute("gameContext");
    String pid = null;

    if(gameContext != null && gameContext.getPid() != null) {
        pid = gameContext.getPid();
    }

    if(gameContext == null) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("pid".equals(cookie.getName())) {
                    pid = cookie.getValue();
                    break;
                }
            }
        }
        if(pid != null) {
            gameContext = new GameContext(pid);
            session.setAttribute("gameContext", gameContext);
        }
    }
    PlayerDAO playerDAO = new PlayerDAO();
    playerDAO.updateLastAccess(pid);
%>

<%-- 로그인 페이지로 이동 --%>
<jsp:forward page="/views/user/login.jsp" />
