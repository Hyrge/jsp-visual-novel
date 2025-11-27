<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="message-box">
    <div class="msg-header">
        <h3>쪽지함</h3>
        <span class="badge-count">2</span> <!-- Unread count -->
    </div>

    <!-- Message List View -->
    <div id="msg-list-view">
        <ul class="msg-list">
            <!-- Quest Message (Unread) -->
            <li class="msg-item unread" onclick="showMsgDetail('quest1')">
                <div class="msg-icon">🏢</div> <!-- Company Icon -->
                <div class="msg-info">
                    <span class="msg-sender">[회사]</span>
                    <span class="msg-title">긴급 공지!! 이번 앨범 컨셉 관련</span>
                    <span class="msg-date">방금 전</span>
                </div>
            </li>
            <!-- Admin Notice (Unread) -->
            <li class="msg-item unread" onclick="showMsgDetail('notice1')">
                <div class="msg-icon">👮</div> <!-- Admin Icon -->
                <div class="msg-info">
                    <span class="msg-sender">[관리자]</span>
                    <span class="msg-title">신고 처리 결과 알림</span>
                    <span class="msg-date">10분 전</span>
                </div>
            </li>
             <!-- System Notice (Read) -->
            <li class="msg-item read" onclick="showMsgDetail('system1')">
                <div class="msg-icon">📢</div> <!-- System Icon -->
                <div class="msg-info">
                    <span class="msg-sender">[SYSTEM]</span>
                    <span class="msg-title">서버 점검 안내</span>
                    <span class="msg-date">어제</span>
                </div>
            </li>
        </ul>
    </div>

    <!-- Message Detail Views (Hidden by default) -->
    
    <!-- Detail: Quest -->
    <div id="msg-detail-quest1" class="msg-detail" style="display:none;">
        <div class="detail-header">
            <button class="btn-back" onclick="showMsgList()">←</button>
            <h4>[회사] 긴급 공지!!</h4>
        </div>
        <div class="detail-content">
            <p>안녕하세요, OOO님.</p>
            <p>이번 앨범 컨셉에 대해 커뮤니티 여론을 확인해주세요.</p>
            <p>퀘스트 목표: 커뮤니티 반응 보고서 작성</p>
        </div>
        <div class="detail-actions">
            <button class="btn-complete" onclick="completeQuest(this)">완료</button>
        </div>
    </div>

    <!-- Detail: Notice -->
    <div id="msg-detail-notice1" class="msg-detail" style="display:none;">
        <div class="detail-header">
            <button class="btn-back" onclick="showMsgList()">←</button>
            <h4>[관리자] 신고 처리 결과</h4>
        </div>
        <div class="detail-content">
            <p>신고하신 게시글이 규정 위반으로 삭제되었습니다.</p>
            <p>깨끗한 커뮤니티를 위해 노력해주셔서 감사합니다.</p>
        </div>
    </div>

    <!-- Detail: System -->
    <div id="msg-detail-system1" class="msg-detail" style="display:none;">
        <div class="detail-header">
            <button class="btn-back" onclick="showMsgList()">←</button>
            <h4>[SYSTEM] 서버 점검 안내</h4>
        </div>
        <div class="detail-content">
            <p>안정적인 서비스를 위해 서버 점검이 진행됩니다.</p>
            <p>일시: 2025.11.28 03:00 ~ 05:00</p>
        </div>
    </div>
</div>

<script>
function showMsgDetail(id) {
    document.getElementById('msg-list-view').style.display = 'none';
    var details = document.getElementsByClassName('msg-detail');
    for(var i=0; i<details.length; i++) {
        details[i].style.display = 'none';
    }
    document.getElementById('msg-detail-' + id).style.display = 'block';
}

function showMsgList() {
    var details = document.getElementsByClassName('msg-detail');
    for(var i=0; i<details.length; i++) {
        details[i].style.display = 'none';
    }
    document.getElementById('msg-list-view').style.display = 'block';
}

function completeQuest(btn) {
    btn.innerText = "완료됨";
    btn.disabled = true;
    btn.style.backgroundColor = "#ccc";
    btn.style.cursor = "default";
    alert("퀘스트가 완료되었습니다!");
}
</script>
