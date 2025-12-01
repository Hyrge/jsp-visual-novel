/**
 * post.js
 * 게시글 관련 기능
 */

// 게시글 추천/비추천
function votePost(postId, voteType) {
    // TODO: 서버에 추천/비추천 요청 전송
    console.log('게시글 투표:', postId, voteType);
    // 실제로는 AJAX로 처리하고 페이지 새로고침 없이 카운트 업데이트
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
