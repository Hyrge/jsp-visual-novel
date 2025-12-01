/**
 * comment.js
 * 댓글 및 답글 관련 기능
 */

// 현재 열려있는 답글 폼 ID 저장
var currentOpenReplyForm = null;

// 답글 폼 토글 (닉네임 클릭 시)
function toggleReplyForm(commentId, username) {
    var replyForm = document.getElementById('replyForm-' + commentId);
    var replyTextarea = document.getElementById('replyContent-' + commentId);
    var replyLengthSpan = document.getElementById('replyLength-' + commentId);

    // 다른 답글 폼이 열려있으면 닫기
    if (currentOpenReplyForm && currentOpenReplyForm !== commentId) {
        closeReplyForm(currentOpenReplyForm);
    }

    // 현재 폼이 닫혀있으면 열기
    if (replyForm.style.display === 'none') {
        replyForm.style.display = 'block';
        currentOpenReplyForm = commentId;

        // @멘션 추가
        var mention = '@' + username + ' ';
        replyTextarea.value = mention;

        // 글자 수 업데이트
        replyLengthSpan.textContent = mention.length;

        // 텍스트 영역에 포커스 + 커서를 멘션 뒤로 이동
        replyTextarea.focus();
        replyTextarea.setSelectionRange(mention.length, mention.length);

        // 글자 수 실시간 카운트 이벤트 리스너 추가 (중복 방지)
        replyTextarea.removeEventListener('input', updateReplyLength);
        replyTextarea.addEventListener('input', updateReplyLength);
    } else {
        // 이미 열려있으면 닫기
        closeReplyForm(commentId);
    }
}

// 답글 글자 수 업데이트 함수
function updateReplyLength(event) {
    var textarea = event.target;
    var commentId = textarea.id.replace('replyContent-', '');
    var lengthSpan = document.getElementById('replyLength-' + commentId);
    lengthSpan.textContent = textarea.value.length;
}

// 답글 폼 닫기
function closeReplyForm(commentId) {
    var replyForm = document.getElementById('replyForm-' + commentId);
    var replyTextarea = document.getElementById('replyContent-' + commentId);
    var replyLengthSpan = document.getElementById('replyLength-' + commentId);

    replyForm.style.display = 'none';
    replyTextarea.value = '';
    replyLengthSpan.textContent = '0';

    if (currentOpenReplyForm === commentId) {
        currentOpenReplyForm = null;
    }
}

// 댓글 삭제
function deleteComment(commentId) {
    if (confirm('정말 삭제하시겠습니까?')) {
        // TODO: 서버에 댓글 삭제 요청 전송
        console.log('댓글 삭제:', commentId);
        location.reload();
    }
}

// 댓글 신고
function reportComment(commentId) {
    if (confirm('이 댓글을 신고하시겠습니까?')) {
        // TODO: 신고 페이지로 이동
        location.href = contextPath + '/views/board/reportBoard.jsp?type=comment&id=' + commentId;
    }
}
