/**
 * post.js
 * 게시글 관련 기능
 */

// 게시글 추천/비추천
function votePost(postId, voteType) {
    // 액션 타입 매핑
    var actionType = (voteType === 'like') ? 'LIKE' : 'DISLIKE';

    // 비동기로 액션 기록
    if (typeof contextPath !== 'undefined') { // postView.jsp에 메타태그나 전역변수로 있다고 가정하거나, 아래처럼 구해야 함
        // contextPath가 없으면 구하기
        var ctx = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));

        fetch(ctx + '/api/action/handleAction.jsp?actionType=' + actionType + '&targetId=' + postId)
            .then(function (res) { return res.json(); })
            .then(function (data) {
                console.log('Vote recorded:', data);
                if (data.success) {
                    // UI 업데이트 (간이)
                    var countEl = document.querySelector('.btn-' + voteType + ' .vote-count');
                    if (countEl) countEl.innerText = parseInt(countEl.innerText) + 1;
                    alert('반영되었습니다.');
                } else {
                    alert('오류가 발생했습니다.');
                }
            })
            .catch(function (err) {
                console.error('Failed to record vote:', err);
            });
    } else {
        // contextPath 구하기 fallback
        var ctx = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));
        fetch(ctx + '/api/action/handleAction.jsp?actionType=' + actionType + '&targetId=' + postId)
            .then(function (res) { return res.json(); })
            .then(function (data) {
                if (data.success) {
                    var countEl = document.querySelector('.btn-' + voteType + ' .vote-count');
                    if (countEl) countEl.innerText = parseInt(countEl.innerText) + 1;
                    alert('반영되었습니다.');
                }
            });
    }
}

// 게시글 신고
function reportPost(postId) {
    if (confirm('이 게시글을 신고하시겠습니까?')) {
        // TODO: 신고 처리
    }
}

// 게시글 삭제
function deletePost(postId) {
    if (confirm('정말 삭제하시겠습니까?')) {
        // TODO: 서버에 삭제 요청 전송
        console.log('게시글 삭제:', postId);
        if (typeof contextPath !== 'undefined') {
            location.href = contextPath + '/views/board/kdolTalkBoard.jsp';
        } else {
            console.error('contextPath가 정의되지 않았습니다.');
        }
    }
}
