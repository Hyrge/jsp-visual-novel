<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.GameContext" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="util.SavePathManager" %>

<jsp:useBean id="gameContext" class="model.GameContext" scope="session" />

<%
    // 시간 스킵 요청 처리
    String skipType = request.getParameter("skipType");

    if ("event".equals(skipType)) {
        // 다음 이벤트로 스킵
        if (gameContext.getGameState().hasNextEvent()) {
            gameContext.getTimeService().skipToNextEvent();
        }
    } else if ("day".equals(skipType)) {
        // 다음날로 스킵
        gameContext.getTimeService().skipToNextDay();
    }

    // 스킵 처리 후 현재 페이지로 리다이렉트 (skipType 파라미터 제거)
    if (skipType != null) {
        String currentUrl = request.getRequestURI();
        String queryString = request.getQueryString();

        if (queryString != null && !queryString.isEmpty()) {
            // skipType 파라미터만 제거
            queryString = queryString.replaceAll("(&?)skipType=[^&]*(&?)", "$2")
                                     .replaceAll("^&|&$", "");
            if (!queryString.isEmpty()) {
                currentUrl += "?" + queryString;
            }
        }

        response.sendRedirect(currentUrl);
        return;
    }

%>
