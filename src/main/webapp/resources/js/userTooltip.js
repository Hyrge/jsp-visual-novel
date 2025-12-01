/**
 * userTooltip.js
 * 닉네임 hover 시 회원정보/쪽지 보내기 메뉴 표시
 */

// 회원정보 보기
function viewUserInfo(username) {
    // TODO: 회원정보 팝업 또는 페이지로 이동
    console.log('회원정보 보기:', username);
    alert(username + '님의 회원정보를 표시합니다.');
}

// 쪽지 보내기
function sendMessage(username) {
    // TODO: 쪽지 보내기 기능 구현
    console.log('쪽지 보내기:', username);
    alert(username + '님에게 쪽지를 보냅니다.');
}

// 닉네임 클릭 시 답글 작성 (기존 기능 유지)
function mentionUserAndReply(commentId, username) {
    // comment.js의 toggleReplyForm 함수 호출
    if (typeof toggleReplyForm === 'function') {
        toggleReplyForm(commentId, username);
    }
}
