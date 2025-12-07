/**
 * post.js
 * 게시글 관련 기능
 */

// 게시글 추천/비추천
function votePost(postId, voteType) {
    var actionType = (voteType === 'like') ? 'LIKE' : 'DISLIKE';
    var ctx = document.querySelector('meta[name="contextPath"]')?.content
              || window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));

    fetch(ctx + '/api/action/handleAction.jsp?actionType=' + actionType + '&targetId=' + postId)
        .then(function (res) { return res.json(); })
        .then(function (data) {
            console.log('[post.js] 응답:', data, 'isRelatedMina:', data.isRelatedMina);
            if (data.success) {
                // UI 업데이트
                var countEl = document.querySelector('.btn-' + voteType + ' .vote-count');
                if (countEl) countEl.innerText = parseInt(countEl.innerText) + 1;

                // 퀘스트 체크 (좋아요/싫어요 둘 다)
                if (window.QuestChecker) {
                    QuestChecker.onLike({
                        postId: postId,
                        isRelatedMina: data.isRelatedMina || false
                    });
                }
            }
        })
        .catch(function (err) {
            console.error('Vote failed:', err);
        });
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
