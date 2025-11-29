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


