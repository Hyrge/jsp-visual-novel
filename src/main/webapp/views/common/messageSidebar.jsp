<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.GameContext" %>
<%@ page import="model.entity.Message" %>
<%@ page import="model.entity.Quest" %>
<%@ page import="model.entity.QuestObjective" %>
<%@ page import="model.enums.QuestStatus" %>
<%@ page import="model.enums.QuestIssuer" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<jsp:useBean id="gameContext" class="model.GameContext" scope="session" />

<%-- 시간 스킵 처리 로직 --%>
<jsp:include page="timeSkipHandler.jsp" />

<%
    List<Message> messages = gameContext.getMessageService().getMessages();
    List<Quest> activeQuests = gameContext.getQuestService().getActiveQuests();
    long unreadCount = messages.stream().filter(m -> !m.isRead()).count();
    int totalCount = messages.size() + activeQuests.size();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
%>

<div class="message-box">
    <!-- Status Section -->
    <div class="status-header">
        <h3>내 정보</h3>
        <div class="status-item">
            <span class="label">평판:</span>
            <span class="value"><%= gameContext.getGameState().getReputation() %></span>
        </div>
    </div>

    <div class="msg-header">
        <h3>쪽지함</h3>
        <span class="badge-count"><%= totalCount %></span>
    </div>

    <!-- 쪽지 + 퀘스트 목록 (통합) -->
    <div id="msg-list-view">
        <ul class="msg-list">
            <%-- ===== 퀘스트를 쪽지처럼 표시 ===== --%>
            <% for (Quest quest : activeQuests) {
                String issuerIcon = quest.getIssuer() == QuestIssuer.SYSTEM ? "⚙️" : "🏢";
                int completed = quest.getCompletedCount();
                int total = quest.getTotalCount();
                int percent = total > 0 ? (completed * 100 / total) : 0;
                String statusClass = quest.getStatus() == QuestStatus.COMPLETABLE ? "completable" : "";
            %>
            <li class="msg-item quest-item <%= statusClass %>" onclick="showQuestDetail('<%= quest.getId() %>')">
                <div class="msg-icon"><%= issuerIcon %></div>
                <div class="msg-info">
                    <span class="msg-sender">[<%= quest.getIssuer() %>]</span>
                    <span class="msg-title"><%= quest.getTitle() %></span>
                    <div class="quest-progress">
                        <div class="quest-progress-bar">
                            <div class="quest-progress-fill" style="width: <%= percent %>%"></div>
                        </div>
                        <span class="quest-progress-text"><%= completed %>/<%= total %></span>
                    </div>
                </div>
            </li>
            <% } %>
            
            <%-- ===== 일반 쪽지 표시 ===== --%>
            <% for (Message msg : messages) {
                String icon = "📢";
                if (msg.getSender() == model.enums.SenderType.ADMIN) {
                    icon = "👮";
                } else if (msg.getSender() == model.enums.SenderType.COMPANY) {
                    icon = "🏢";
                }
            %>
            <li class="msg-item <%= msg.isRead() ? "read" : "unread" %>" onclick="showMsgDetail('<%= msg.getId() %>')">
                <div class="msg-icon"><%= icon %></div>
                <div class="msg-info">
                    <span class="msg-sender">[<%= msg.getSender() %>]</span>
                    <span class="msg-title"><%= msg.getTitle() %></span>
                    <span class="msg-date"><%= msg.getCreatedAt().format(formatter) %></span>
                </div>
            </li>
            <% } %>
            
            <%-- 둘 다 없으면 빈 메시지 --%>
            <% if (messages.isEmpty() && activeQuests.isEmpty()) { %>
            <li class="msg-item msg-item-empty">
                <span class="msg-empty-text">메시지가 없습니다.</span>
            </li>
            <% } %>
        </ul>
        
        <!-- 쪽지 보내기 버튼 (목록 아래) -->
        <div class="msg-send-btn-area">
            <button type="button" class="btn-send-msg" onclick="openSendMsgPopup()">✉️ 쪽지 보내기</button>
        </div>
    </div>

    <%-- ===== 퀘스트 상세 뷰 ===== --%>
    <% for (Quest quest : activeQuests) { %>
    <div id="quest-detail-<%= quest.getId() %>" class="msg-detail" style="display: none;">
        <div class="detail-header">
            <button class="btn-back" onclick="showMsgList()">←</button>
            <h4>[<%= quest.getIssuer() %>] <%= quest.getTitle() %></h4>
        </div>
        
        <div class="detail-content">
            <p><%= quest.getDescription() != null ? quest.getDescription().replace("\n", "<br>") : "" %></p>
            
            <%-- 상세보기 프로그레스바 --%>
            <%
                int detailCompleted = quest.getCompletedCount();
                int detailTotal = quest.getTotalCount();
                int detailPercent = detailTotal > 0 ? (detailCompleted * 100 / detailTotal) : 0;
            %>
            <div class="quest-progress" style="margin: 10px 0 20px 0;">
                <div class="quest-progress-bar">
                    <div class="quest-progress-fill" style="width: <%= detailPercent %>%"></div>
                </div>
                <span class="quest-progress-text"><%= detailCompleted %>/<%= detailTotal %></span>
            </div>
            
            <% if (quest.getObjectives() != null && !quest.getObjectives().isEmpty()) { %>
            <ul class="quest-objectives">
                <% for (QuestObjective obj : quest.getObjectives()) { 
                    if (!obj.isVisible()) continue;
                %>
                <li class="<%= obj.isCompleted() ? "completed" : "" %>">
                    <span class="objective-check"><%= obj.isCompleted() ? "✓" : "○" %></span>
                    <span class="objective-text"><%= obj.getDescription() %></span>
                </li>
                <% } %>
            </ul>
            <% } %>
        </div>
        
        <% if (quest.getStatus() == QuestStatus.COMPLETABLE) { %>
        <div class="detail-actions">
            <form action="<%= request.getContextPath() %>/api/quest/completeQuest.jsp" method="post">
                <input type="hidden" name="questId" value="<%= quest.getId() %>">
                <button type="submit" class="btn-complete">
                    완료하기 (+<%= quest.getRewardReputation() %> 평판)
                </button>
            </form>
        </div>
        <% } %>
    </div>
    <% } %>

    <%-- ===== 쪽지 상세 뷰 ===== --%>
    <% for (Message msg : messages) { %>
    <div id="msg-detail-<%= msg.getId() %>" class="msg-detail" style="display: none;">
        <div class="detail-header">
            <button class="btn-back" onclick="showMsgList()">←</button>
            <h4>[<%= msg.getSender() %>] <%= msg.getTitle() %></h4>
        </div>
        <div class="detail-content">
            <p><%= msg.getContent().replace("\n", "<br>") %></p>
        </div>
        <% if (msg.getRelatedQuestIds() != null && !msg.getRelatedQuestIds().isEmpty()) { %>
        <div class="detail-actions">
            <button class="btn-complete" onclick="showQuestDetail('<%= msg.getRelatedQuestIds().get(0) %>')">
                퀘스트 확인
            </button>
        </div>
        <% } %>
    </div>
    <% } %>
</div>

<%-- 시간 스킵 컨트롤 패널 --%>
<jsp:include page="timeControlPanel.jsp" />

<script src="<%= request.getContextPath() %>/resources/js/charCounter.js?v=2"></script>
<script>
// 최소한의 JS만 사용
function showMsgList() {
    document.getElementById('msg-list-view').style.display = 'block';
    document.querySelectorAll('.msg-detail').forEach(function(el) {
        el.style.display = 'none';
    });
}

function showMsgDetail(id) {
    document.getElementById('msg-list-view').style.display = 'none';
    document.querySelectorAll('.msg-detail').forEach(function(el) {
        el.style.display = 'none';
    });
    var detail = document.getElementById('msg-detail-' + id);
    if (detail) detail.style.display = 'block';
}

function showQuestDetail(id) {
    document.getElementById('msg-list-view').style.display = 'none';
    document.querySelectorAll('.msg-detail').forEach(function(el) {
        el.style.display = 'none';
    });
    var detail = document.getElementById('quest-detail-' + id);
    if (detail) detail.style.display = 'block';
}

function toggleMessageSidebar() {
    var sidebar = document.querySelector('.sidebar-area');
    if (sidebar) {
        sidebar.classList.toggle('hidden');
    }
}

// 쪽지 보내기 팝업 동적 로드
function openSendMsgPopup() {
    // 이미 로드됐으면 보여주기만
    var existing = document.getElementById('sendMsgPopup');
    if (existing) {
        existing.style.display = 'flex';
        return;
    }
    
    // fetch로 팝업 HTML 로드
    var contextPath = '<%= request.getContextPath() %>';
    fetch(contextPath + '/views/common/sendMessagePopup.jsp')
        .then(function(response) { return response.text(); })
        .then(function(html) {
            // body에 팝업 추가
            var div = document.createElement('div');
            div.innerHTML = html;
            document.body.appendChild(div);
            // 팝업 표시
            var popup = document.getElementById('sendMsgPopup');
            if (popup) popup.style.display = 'flex';
        })
        .catch(function(err) {
            alert('팝업 로드 실패: ' + err);
        });
}

function closeSendMsgPopup() {
    var popup = document.getElementById('sendMsgPopup');
    if (popup) popup.style.display = 'none';
}

function sendMessage(event) {
    event.preventDefault();
    
    // 팝업 내 input 값 가져오기 (필요시)
    // var content = document.querySelector('#sendMsgPopup textarea').value;
    
    // 액션 기록
    var contextPath = '<%= request.getContextPath() %>';
    fetch(contextPath + '/api/action/handleAction.jsp?actionType=SEND_MESSAGE&content=message_sent')
        .then(function(res) { return res.json(); })
        .then(function(data) {
            console.log('Message action recorded:', data);
            alert('쪽지를 보냈습니다.');
            closeSendMsgPopup();
        })
        .catch(function(err) {
            console.error('Failed to record message action:', err);
            alert('쪽지 전송 중 오류가 발생했습니다.');
        });
        
    return false;
}
</script>