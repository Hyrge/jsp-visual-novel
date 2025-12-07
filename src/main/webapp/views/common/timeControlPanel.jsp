<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.GameContext" %>

<jsp:useBean id="gameContext" class="model.GameContext" scope="session" />

<%
    // 세션의 gameContext에서 직접 hasNextEvent 확인
    boolean hasNextEvent = gameContext.getGameState().hasNextEvent();
%>

<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/css/timeControlPanel.css">

<!-- 시간 스킵 컨트롤 패널 -->
<div class="time-control-panel">
    <div class="time-display">
        <%
            // 날짜 포맷팅 (예: 2025년 9월 1일 (월))
            java.time.format.DateTimeFormatter dateFormatter =
                java.time.format.DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)", java.util.Locale.KOREAN);
            String formattedDate = gameContext.getGameState().getCurrentDate().format(dateFormatter);

            // 시간 포맷팅 (예: 09:00)
            java.time.format.DateTimeFormatter timeFormatter =
                java.time.format.DateTimeFormatter.ofPattern("HH:mm");
            String formattedTime = gameContext.getGameState().getCurrentTime().format(timeFormatter);
        %>
        <div class="current-date">
            <span class="date-label">현재:</span>
            <span class="date-value"><%= formattedDate %> <%= formattedTime %></span>
        </div>

    </div>

    <div class="time-actions">
        <!-- 다음 이벤트 버튼 -->
        <form method="get" style="display: inline;">
            <%
                // 기존 쿼리 파라미터 유지
                java.util.Map<String, String[]> paramMap = request.getParameterMap();
                for (java.util.Map.Entry<String, String[]> entry : paramMap.entrySet()) {
                    String paramName = entry.getKey();
                    if (!"skipType".equals(paramName)) {
                        String[] values = entry.getValue();
                        if (values != null && values.length > 0) {
                            for (String value : values) {
            %>
            <input type="hidden" name="<%= paramName %>" value="<%= value %>">
            <%
                            }
                        }
                    }
                }
            %>
            <input type="hidden" name="skipType" value="event">
            <button type="submit" class="btn-time-skip" <%= hasNextEvent ? "" : "disabled" %>>
                <span class="skip-icon">▶</span>
                <span class="skip-text">다음 이벤트</span>
            </button>
        </form>

        <!-- 다음날 버튼 -->
        <form method="get" style="display: inline;">
            <%
                for (java.util.Map.Entry<String, String[]> entry : paramMap.entrySet()) {
                    String paramName = entry.getKey();
                    if (!"skipType".equals(paramName)) {
                        String[] values = entry.getValue();
                        if (values != null && values.length > 0) {
                            for (String value : values) {
            %>
            <input type="hidden" name="<%= paramName %>" value="<%= value %>">
            <%
                            }
                        }
                    }
                }
            %>
            <input type="hidden" name="skipType" value="day">
            <button type="submit" class="btn-day-skip">
                <span class="skip-icon">⏩</span>
                <span class="skip-text">다음날</span>
            </button>
        </form>
    </div>
</div>
