<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%-- 쪽지 보내기 팝업 --%>
<div id="sendMsgPopup" style="display:none; position:fixed; top:0; left:0; right:0; bottom:0; background:rgba(0,0,0,0.5); z-index:9999; align-items:center; justify-content:center;" onclick="if(event.target===this) closeSendMsgPopup()">
    <div class="msg-popup">
        <div class="msg-popup-header">
            <h4>쪽지 보내기</h4>
            <button class="msg-popup-close" onclick="closeSendMsgPopup()">&times;</button>
        </div>
        <div class="msg-popup-body">
            <form id="sendMessageForm" onsubmit="return sendMessage(event)">
                <div class="form-group">
                    <label for="recipientId">받는 사람 ID:</label>
                    <input type="text" id="recipientId" name="recipientId" placeholder="사용자 ID 입력" required maxlength="50">
                </div>
                <div class="form-group">
                    <label for="messageContent">메시지:</label>
                    <textarea id="messageContent" name="messageContent" placeholder="메시지 내용 (최대 500자)" required maxlength="500" rows="4"></textarea>
                    <div class="char-count">
                        <span id="msgCharCount">0</span> / 500
                    </div>
                </div>
                <button type="submit" class="btn-send-msg">보내기</button>
            </form>
        </div>
    </div>
</div>

<script>
function openSendMsgPopup() {
    document.getElementById('sendMsgPopup').style.display = 'flex';
}

function closeSendMsgPopup() {
    document.getElementById('sendMsgPopup').style.display = 'none';
}

function sendMessage(event) {
    event.preventDefault();
    alert('쪽지 전송 기능은 구현 중입니다.');
    closeSendMsgPopup();
    return false;
}
</script>
