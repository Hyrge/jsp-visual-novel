/**
 * userTooltip.js
 * 닉네임 hover 시 회원정보/쪽지 보내기 메뉴 표시
 */

// 회원정보 보기
function viewUserInfo(username) {
    // TODO: 회원정보 팝업 또는 페이지로 이동
    console.log('회원정보 보기:', username);

    // 회원정보 모달 표시
    viewUserInfo(userId, username);
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

// 회원정보 모달 표시
function viewUserInfo(userId, username) {
    // TODO: 서버에서 회원 정보 가져오기 (현재는 더미 데이터 사용)
    var userInfo = {
        id: userId || 'user123',
        username: username,
        signupDate: '2024.11.15',
        description: '안녕하세요! 케이팝을 사랑하는 팬입니다.'
    };

    // 모달 HTML 생성
    var modalHTML = `
        <div id="userInfoModal" class="modal-overlay">
            <div class="modal-content user-info-modal">
                <div class="modal-header">
                    <h3>회원 정보</h3>
                    <button class="modal-close" onclick="closeUserInfoModal()">&times;</button>
                </div>
                <div class="modal-body">
                    <div class="user-info-row">
                        <span class="user-info-label">닉네임</span>
                        <span class="user-info-value">${userInfo.username}</span>
                    </div>
                    <div class="user-info-row">
                        <span class="user-info-label">아이디</span>
                        <span class="user-info-value">${userInfo.id}</span>
                    </div>
                    <div class="user-info-row">
                        <span class="user-info-label">가입일</span>
                        <span class="user-info-value">${userInfo.signupDate}</span>
                    </div>
                    <div class="user-info-row description">
                        <span class="user-info-label">자기소개</span>
                        <p class="user-info-value">${userInfo.description}</p>
                    </div>
                </div>
            </div>
        </div>
    `;

    // 기존 모달이 있으면 제거
    var existingModal = document.getElementById('userInfoModal');
    if (existingModal) {
        existingModal.remove();
    }

    // 모달을 body에 추가
    document.body.insertAdjacentHTML('beforeend', modalHTML);

    // 모달 외부 클릭 시 닫기
    document.getElementById('userInfoModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeUserInfoModal();
        }
    });
}

// 회원정보 모달 닫기
function closeUserInfoModal() {
    var modal = document.getElementById('userInfoModal');
    if (modal) {
        modal.remove();
    }
}
