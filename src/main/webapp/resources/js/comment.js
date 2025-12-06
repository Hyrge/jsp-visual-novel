// 현재 열려있는 답글 폼 ID 저장
var currentOpenReplyForm = null;

// 답글 폼 토글 (닉네임 클릭 시)
function toggleReplyForm(commentId, username) {
    var replyForm = document.getElementById('replyForm-' + commentId);
    var replyEditor = document.getElementById('replyContent-' + commentId);
    var replyLengthSpan = document.getElementById('replyLength-' + commentId);

    // 다른 답글 폼이 열려있으면 닫기
    if (currentOpenReplyForm && currentOpenReplyForm !== commentId) {
        closeReplyForm(currentOpenReplyForm);
    }

    // 현재 폼이 닫혀있으면 열기
    if (replyForm.style.display === 'none') {
        replyForm.style.display = 'block';
        currentOpenReplyForm = commentId;

        // @멘션을 강조된 span으로 추가
        var mentionHtml = '<span class="mention" contenteditable="false">@' + username + '</span>&nbsp;';
        replyEditor.innerHTML = mentionHtml;

        // 글자 수 업데이트
        var textLength = replyEditor.innerText.trim().length;
        replyLengthSpan.textContent = textLength;

        // 에디터에 포커스 + 커서를 멘션 뒤로 이동
        replyEditor.focus();
        placeCaretAtEnd(replyEditor);
    } else {
        // 이미 열려있으면 닫기
        closeReplyForm(commentId);
    }
}

// 커서를 요소 맨 끝으로 이동
function placeCaretAtEnd(el) {
    el.focus();
    var range = document.createRange();
    range.selectNodeContents(el);
    range.collapse(false);
    var sel = window.getSelection();
    sel.removeAllRanges();
    sel.addRange(range);
}

// 답글 입력 핸들러 (실시간 @멘션 감지)
function handleReplyInput(commentId) {
    var replyEditor = document.getElementById('replyContent-' + commentId);
    var replyLengthSpan = document.getElementById('replyLength-' + commentId);
    var replyInput = document.getElementById('replyInput-' + commentId);

    // 글자 수 업데이트
    var textLength = replyEditor.innerText.trim().length;
    replyLengthSpan.textContent = Math.min(textLength, 500);

    // hidden input에 텍스트 내용 저장 (폼 제출용)
    replyInput.value = replyEditor.innerText.trim();
}

// 키보드 이벤트 핸들러 (Enter 제출 방지 등)
function handleReplyKeydown(event, commentId) {
    if (event.key === 'Enter' && !event.shiftKey) {
        // Shift 없이 Enter 시 줄바꿈 대신 아무것도 안 함 (또는 제출)
        // event.preventDefault();
    }

    // 글자 수 제한
    var replyEditor = document.getElementById('replyContent-' + commentId);
    var textLength = replyEditor.innerText.trim().length;
    if (textLength >= 500 && event.key !== 'Backspace' && event.key !== 'Delete') {
        event.preventDefault();
    }
}

// 답글 폼 닫기
function closeReplyForm(commentId) {
    var replyForm = document.getElementById('replyForm-' + commentId);
    var replyEditor = document.getElementById('replyContent-' + commentId);
    var replyLengthSpan = document.getElementById('replyLength-' + commentId);

    replyForm.style.display = 'none';
    replyEditor.innerHTML = '';
    replyLengthSpan.textContent = '0';

    if (currentOpenReplyForm === commentId) {
        currentOpenReplyForm = null;
    }
}

// 답글 유효성 검사 (폼 제출 전)
function validateReply(commentId) {
    var replyEditor = document.getElementById('replyContent-' + commentId);
    var replyInput = document.getElementById('replyInput-' + commentId);
    var content = replyEditor.innerText.trim();

    if (!content || content.length === 0) {
        alert('답글 내용을 입력해주세요.');
        return false;
    }

    if (content.length > 500) {
        alert('답글은 500자를 초과할 수 없습니다.');
        return false;
    }

    // hidden input에 최종 내용 저장
    replyInput.value = content;
    return true;
}

// 댓글 삭제
function deleteComment(commentId) {
    if (confirm('정말 삭제하시겠습니까?')) {
        console.log('댓글 삭제:', commentId);
        location.reload();
    }
}

// 댓글 신고
function reportComment(commentId) {
    if (confirm('이 댓글을 신고하시겠습니까?')) {
        // TODO: 신고 처리
    }
}

// @멘션 클릭 시 부모 댓글로 스크롤
function scrollToComment(commentId) {
    if (!commentId) return;

    var targetComment = document.getElementById(commentId);
    if (targetComment) {
        targetComment.scrollIntoView({ behavior: 'smooth', block: 'center' });

        targetComment.style.transition = 'background-color 0.3s ease';
        targetComment.style.backgroundColor = '#fff3cd';

        setTimeout(function () {
            targetComment.style.backgroundColor = '';
        }, 1500);
    }
}
