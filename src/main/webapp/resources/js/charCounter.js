/**
 * charCounter.js
 * 글자 수 카운터 초기화 및 관리
 */

// 댓글 글자 수 카운터 초기화
function initCommentCharCounter() {
    var commentTextarea = document.getElementById('commentContent');
    var currentLengthSpan = document.getElementById('currentLength');

    if (commentTextarea && currentLengthSpan) {
        commentTextarea.addEventListener('input', function() {
            currentLengthSpan.textContent = this.value.length;
        });
    }
}

// 게시글 글자 수 카운터 초기화 (게시글 작성 화면에서 사용)
function initPostCharCounter() {
    var titleInput = document.getElementById('postTitle');
    var contentTextarea = document.getElementById('postContent');
    var titleLengthSpan = document.getElementById('titleLength');
    var contentLengthSpan = document.getElementById('contentLength');

    if (titleInput && titleLengthSpan) {
        titleInput.addEventListener('input', function() {
            titleLengthSpan.textContent = this.value.length;
        });
    }

    if (contentTextarea && contentLengthSpan) {
        contentTextarea.addEventListener('input', function() {
            contentLengthSpan.textContent = this.value.length;
        });
    }
}

// 쪽지 글자 수 카운터 초기화
function initMessageCharCounter() {
    var messageTextarea = document.getElementById('messageContent');
    var msgCharCount = document.getElementById('msgCharCount');

    if (messageTextarea && msgCharCount) {
        messageTextarea.addEventListener('input', function() {
            msgCharCount.textContent = this.value.length;
        });
        // 초기값 설정
        msgCharCount.textContent = messageTextarea.value.length;
    }
}

// 페이지 로드 시 자동 초기화
document.addEventListener('DOMContentLoaded', function() {
    initCommentCharCounter();
    initPostCharCounter();
    initMessageCharCounter();
});
