<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.UUID" %>
<%@ page import="manager.DataManager" %>
<%@ page import="dao.PlayerDAO" %>
<%@ page import="util.SavePathManager" %>
<%@ page import="model.GameContext" %>
<%@ page import="manager.GameController" %>

<%
    GameController gameController = GameController.getInstance();
    GameContext gameContext = session.getAttribute("gameContext");
    if (gameContext == null) {
        gameContext = new GameContext();
        session.setAttribute("gameContext", gameContext);
    }


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

    boolean isNewPlayer = false;
    if (pid == null) {
        pid = UUID.randomUUID().toString();
        Cookie pidCookie = new Cookie("pid", pid);
        pidCookie.setMaxAge(60 * 60 * 24 * 15);
        pidCookie.setPath("/");
        response.addCookie(pidCookie);
        isNewPlayer = true;
        gameContext.setPid(pid);
        gameController.createPlayer(pid);
    } else {
        gameContext.setPid(pid);
        playerDAO.updateLastAccess(pid);
    }
%>

<%-- 로그인 페이지로 이동 --%>
<jsp:forward page="/views/user/login.jsp" />
