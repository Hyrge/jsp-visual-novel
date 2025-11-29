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

