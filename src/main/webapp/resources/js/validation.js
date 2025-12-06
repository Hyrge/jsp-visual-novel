/**
 * validation.js
 * 폼 유효성 검사 관련 함수들
 */

// 댓글 유효성 검사
// 댓글 유효성 검사
function validateComment() {
    var content = document.getElementById('commentContent').value.trim();
    if (content.length === 0) {
        alert('댓글 내용을 입력해주세요.');
        return false;
    }
    if (content.length > 500) {
        alert('댓글은 500자 이내로 작성해주세요.');
        return false;
    }

    // 액션 기록 (비동기, 페이지 이동에도 유지)
    if (typeof contextPath === 'undefined') {
        var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));
    }

    fetch(contextPath + '/api/action/handleAction.jsp?actionType=CREATE_COMMENT&content=' + encodeURIComponent(content) + '&targetId=t2', { keepalive: true })
        .catch(err => console.error(err));

    return true;
}

// 답글 유효성 검사
function validateReply(commentId) {
    var content = document.getElementById('replyContent-' + commentId).value.trim();
    if (content.length === 0) {
        alert('답글 내용을 입력해주세요.');
        return false;
    }
    if (content.length > 500) {
        alert('답글은 500자 이내로 작성해주세요.');
        return false;
    }
    return true;
}

// 게시글 유효성 검사 (게시글 작성 화면에서 사용)
function validatePost() {
    var title = document.getElementById('postTitle').value.trim();
    var content = document.getElementById('postContent').value.trim();

    if (title.length === 0) {
        alert('제목을 입력해주세요.');
        return false;
    }
    if (title.length > 100) {
        alert('제목은 100자 이내로 작성해주세요.');
        return false;
    }
    if (content.length === 0) {
        alert('내용을 입력해주세요.');
        return false;
    }
    if (content.length > 5000) {
        alert('내용은 5000자 이내로 작성해주세요.');
        return false;
    }
    return true;
}
