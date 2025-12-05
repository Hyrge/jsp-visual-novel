<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.GameContext" %>
<%@ page import="model.entity.Message" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<jsp:useBean id="gameContext" class="model.GameContext" scope="session" />

<div class="message-box">
    <!-- Status Section -->
    <div class="status-header">
        <h3>내 정보</h3>
        <div class="status-item">
            <span class="label">평판:</span>
            <span class="value">
                <%= gameContext.getGameState().getReputation() %>
            </span>
        </div>
    </div>

    <div class="msg-header">
        <h3>쪽지함</h3>
        <%
            List<Message> messages = gameContext.getMessageService().getMessages();
            long unreadCount = messages.stream()
                    .filter(m -> !m.isRead())
                    .count();
        %>
        <span class="badge-count">
            <%= unreadCount %>
        </span>
    </div>

    <!-- 쪽지 보내기 폼 -->
    <div class="msg-send-form">
        <h4>쪽지 보내기</h4>
        <form id="sendMessageForm" onsubmit="return sendMessage(event)">
            <div class="form-group">
                <label for="recipientId">받는 사람 ID:</label>
                <input type="text" id="recipientId" name="recipientId" placeholder="사용자 ID 입력" required maxlength="50">
            </div>
            <div class="form-group">
                <label for="messageContent">메시지:</label>
                <textarea id="messageContent" name="messageContent" placeholder="메시지 내용 (최대 500자)" required maxlength="500" rows="3"></textarea>
                <div class="char-count">
                    <span id="msgCharCount">0</span> / 500
                </div>
            </div>
            <button type="submit" class="btn-send-msg">보내기</button>
        </form>
    </div>

    <!-- Message List View -->
    <div id="msg-list-view">
        <ul class="msg-list">
            <%
                if (messages.isEmpty()) {
            %>
            <li class="msg-item msg-item-empty">
                <span class="msg-empty-text">메시지가 없습니다.</span>
            </li>
            <%
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");

                    for (Message msg : messages) {
                        String icon = "📢";
                        if (msg.getSender() == model.enums.SenderType.ADMIN) {
                            icon = "👮";
                        } else if (msg.getSender() == model.enums.SenderType.COMPANY) {
                            icon = "🏢";
                        }
            %>
            <li class="msg-item <%= msg.isRead() ? " read" : "unread" %>"
                onclick="showMsgDetail('<%= msg.getId() %>')">
                <div class="msg-icon">
                    <%= icon %>
                </div>
                <div class="msg-info">
                    <span class="msg-sender">[<%= msg.getSender() %>]</span>
                    <span class="msg-title">
                        <%= msg.getTitle() %>
                    </span>
                    <span class="msg-date">
                        <%= msg.getCreatedAt().format(formatter) %>
                    </span>
                </div>
            </li>
            <%
                    }
                }
            %>
        </ul>
    </div>

    <!-- Message Detail Views -->
    <%
        for (Message msg : messages) {
    %>
    <div id="msg-detail-<%= msg.getId() %>" class="msg-detail">
        <div class="detail-header">
            <button class="btn-back" onclick="showMsgList()">←</button>
            <h4>[<%= msg.getSender() %>] <%= msg.getTitle() %></h4>
        </div>
        <div class="detail-content">
            <p>
                <%= msg.getContent().replace("\n", "<br>" ) %>
            </p>
        </div>
        <%
            if (msg.getRelatedQuestIds() != null && !msg.getRelatedQuestIds().isEmpty()) {
        %>
        <div class="detail-actions">
            <button class="btn-complete"
                    onclick="completeQuest(this, '<%= msg.getRelatedQuestIds().get(0) %>')">
                퀘스트 확인
            </button>
        </div>
        <%
            }
        %>
    </div>
    <%
        }
    %>
</div>

<!-- 시간 스킵 컨트롤 패널 -->
<div class="time-control-panel">
    <div class="time-display">
        <%
            // 날짜 포맷팅 (예: 2025년 9월 1일 (월))
            java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)", java.util.Locale.KOREAN);
            String formattedDate = gameContext.getGameState().getCurrentDate().format(dateFormatter);

            // 시간 포맷팅 (예: 09:00)
            java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
            String formattedTime = gameContext.getGameState().getCurrentTime().format(timeFormatter);
        %>
        <div class="current-date">
            <span class="date-label">현재:</span>
            <span class="date-value"><%= formattedDate %> <%= formattedTime %></span>
        </div>
        <div class="d-day-counter">
            <span class="d-day-label">앨범 발매</span>
            <span class="d-day-value"><%= gameContext.getGameState().getDDayText() %></span>
        </div>
    </div>

    <div class="time-actions">
        <button class="btn-time-skip" onclick="skipToNextEvent()">
            <span class="skip-icon">▶</span>
            <span class="skip-text">다음 이벤트</span>
        </button>
        <button class="btn-day-skip" onclick="skipToNextDay()">
            <span class="skip-icon">⏩</span>
            <span class="skip-text">다음날</span>
        </button>
    </div>
</div>

<script src="<%= request.getContextPath() %>/resources/js/charCounter.js?v=2"></script>
<script src="<%= request.getContextPath() %>/resources/js/messageSidebar.js?v=2"></script>