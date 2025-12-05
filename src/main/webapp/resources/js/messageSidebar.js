function showMsgDetail(id) {
    document.getElementById('msg-list-view').style.display = 'none';

    var details = document.getElementsByClassName('msg-detail');
    for (var i = 0; i < details.length; i++) {
        details[i].style.display = 'none';
    }

    var detail = document.getElementById('msg-detail-' + id);
    if (detail) {
        detail.style.display = 'block';
    }
}

function showMsgList() {
    var details = document.getElementsByClassName('msg-detail');
    for (var i = 0; i < details.length; i++) {
        details[i].style.display = 'none';
    }

    document.getElementById('msg-list-view').style.display = 'block';
}

function completeQuest(btn, questId) {
    // TODO: Implement quest completion via AJAX
    alert("퀘스트 기능은 아직 구현 중입니다: " + questId);
}

// 쪽지함 사이드바 토글 함수
function toggleMessageSidebar() {
    var sidebar = document.querySelector('.sidebar-area');
    if (sidebar) {
        sidebar.classList.toggle('hidden');
    }
}

// 쪽지 보내기
function sendMessage(event) {
    event.preventDefault();

    var recipientId = document.getElementById('recipientId').value.trim();
    var messageContent = document.getElementById('messageContent').value.trim();

    // 유효성 검사
    if (!recipientId || !messageContent) {
        alert('받는 사람 ID와 메시지를 모두 입력해주세요.');
        return false;
    }

    if (messageContent.length > 500) {
        alert('메시지는 최대 500자까지 입력 가능합니다.');
        return false;
    }

    // TODO: AJAX로 서버에 쪽지 전송
    alert('쪽지 전송 기능은 구현 중입니다.\n받는 사람: ' + recipientId + '\n메시지: ' + messageContent);

    // 폼 초기화
    document.getElementById('sendMessageForm').reset();
    // 글자 수 카운터 초기화 (charCounter.js의 함수 재사용)
    var msgCharCount = document.getElementById('msgCharCount');
    if (msgCharCount) {
        msgCharCount.textContent = '0';
    }

    return false;
}

