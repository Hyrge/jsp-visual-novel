<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.UUID" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="manager.DataManager" %>

<%
    String realPath = application.getRealPath("/");
    try {
        Class<?> controllerClass = Class.forName("GameController");
        Method getInstanceMethod = controllerClass.getMethod("getInstance");
        Object instance = getInstanceMethod.invoke(null);
        Method initMethod = controllerClass.getMethod("init", String.class);
        initMethod.invoke(instance, realPath);
    } catch (Exception e) {
        e.printStackTrace();
    }
%>

<%
    // 1. 플레이어 식별자(PID) 확인 및 생성
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
        pid = UUID.randomUUID().toString();
        Cookie pidCookie = new Cookie("pid", pid);
        pidCookie.setMaxAge(60 * 60 * 24 * 10); // 10일
        pidCookie.setPath("/");
        response.addCookie(pidCookie);
    }
%>

<%-- 2. GameContext 초기화 (jsp:useBean 사용) --%>
<jsp:useBean id="gameContext" class="model.GameContext" scope="session" />
<%
    gameContext.init(pid, DataManager.getInstance());
    out.println("GameContext PID: " + gameContext.getPid());
%>

<%-- 3. 로그인 페이지로 이동 (Forward 사용) --%>
<jsp:forward page="/views/user/login.jsp" />
