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
    // 현재 URL의 파라미터 확인
    var urlParams = new URLSearchParams(window.location.search);
    var showMessages = urlParams.get('showMessages');

    // 현재 상태 반전
    var newState = (showMessages === 'true') ? 'false' : 'true';

    // 파라미터 업데이트
    urlParams.set('showMessages', newState);

    // 페이지 리로드 (파라미터 포함)
    window.location.search = urlParams.toString();
}

// 다음 이벤트로 시간 스킵
function skipToNextEvent() {
    // TODO: 이벤트 큐의 다음 이벤트 시간으로 이동
    // 서버에 AJAX 요청을 보내서 시간을 스킵하고 발생한 이벤트를 처리
    if (confirm('다음 이벤트 시간으로 이동하시겠습니까?')) {
        // AJAX 요청 구현 예정
        alert('다음 이벤트 스킵 기능은 구현 중입니다.');
        // location.href = contextPath + '/timeSkip.jsp?type=event';
    }
}

// 다음날로 스킵
function skipToNextDay() {
    // TODO: 하루 전체를 스킵하고 일일 정산 실행
    if (confirm('하루를 스킵하시겠습니까?\n스킵하는 동안 발생한 이벤트가 자동으로 처리됩니다.')) {
        // AJAX 요청 구현 예정
        alert('다음날 스킵 기능은 구현 중입니다.');
        // location.href = contextPath + '/timeSkip.jsp?type=day';
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

