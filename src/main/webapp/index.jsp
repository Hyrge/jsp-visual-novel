<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.UUID" %>
<%@ page import="manager.GameController" %>
<%@ page import="manager.DataManager" %>
<%@ page import="dao.PlayerDAO" %>
<%@ page import="util.SavePathManager" %>

<%
    GameController.getInstance();

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
        pidCookie.setMaxAge(60 * 60 * 24 * 10);
        pidCookie.setPath("/");
        response.addCookie(pidCookie);
        isNewPlayer = true;
    }

    PlayerDAO playerDAO = new PlayerDAO();
    if (isNewPlayer || !playerDAO.exists(pid)) {
        String savePath = "saves/" + pid;
        boolean created = playerDAO.createPlayer(pid, savePath);

        if (created) {
            // saves/{pid} 폴더 생성
            String basePath = application.getRealPath("/");
            SavePathManager.createPlayerSaveFolder(basePath, pid);
            out.println("<!-- 플레이어 자동 가입 완료: " + pid + " -->");
        } else {
            out.println("<!-- 플레이어 가입 실패 -->");
        }
    } else {
        playerDAO.updateLastAccess(pid);
        out.println("<!-- 기존 플레이어 접속: " + pid + " -->");
    }
%>

<%-- GameContext 초기화 --%>
<jsp:useBean id="gameContext" class="model.GameContext" scope="session" />
<%
    gameContext.init(pid, DataManager.getInstance());
    out.println("<!-- GameContext PID: " + gameContext.getPid() + " -->");
%>

<%-- 로그인 페이지로 이동 --%>
<jsp:forward page="/views/user/login.jsp" />
