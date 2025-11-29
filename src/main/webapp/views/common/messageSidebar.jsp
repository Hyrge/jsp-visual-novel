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
            List<Message> messages = gameContext.getMessageManager().getMessages();
            long unreadCount = messages.stream()
                    .filter(m -> !m.isRead())
                    .count();
        %>
        <span class="badge-count">
            <%= unreadCount %>
        </span>
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

<script src="<%= request.getContextPath() %>/resources/js/messageSidebar.js"></script>