/**
 * userTooltip.js
 * 닉네임 hover 시 회원정보/쪽지 보내기 메뉴 표시
 */

// 회원정보 보기 (playerId와 nickname을 받아서 서버에서 정보 조회)
function viewUserInfo(playerId, nickname) {
    // API 호출하여 회원 정보 가져오기
    var contextPath = document.querySelector('meta[name="contextPath"]')?.content || '';
    var apiUrl = contextPath + '/api/getUserInfo.jsp?id=' + encodeURIComponent(playerId) + '&nickname=' + encodeURIComponent(nickname || '');

    console.log('[viewUserInfo] playerId:', playerId, ', nickname:', nickname);
    console.log('[viewUserInfo] API URL:', apiUrl);

    fetch(apiUrl)
        .then(function (response) {
            return response.json();
        })
        .then(function (userInfo) {
            console.log('[viewUserInfo] API Response:', userInfo);
            if (userInfo.error) {
                alert(userInfo.error);
                return;
            }
            showUserInfoModal(userInfo);
        })
        .catch(function (error) {
            console.error('회원정보 조회 오류:', error);
            // 오류 시 기본 정보로 표시
            showUserInfoModal({
                nickname: nickname || '유저',
                signupDate: '알 수 없음',
                description: ''
            });
        });
}

// 회원정보 모달 표시
function showUserInfoModal(userInfo) {
    // 아이디 결정: NPC면 id, 플레이어면 userId
    var displayId = userInfo.userId || userInfo.id || '';

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
                        <span class="user-info-value">${userInfo.nickname || '유저'}</span>
                    </div>
                    ${displayId ? `
                    <div class="user-info-row">
                        <span class="user-info-label">아이디</span>
                        <span class="user-info-value">${displayId}</span>
                    </div>
                    ` : ''}
                    <div class="user-info-row">
                        <span class="user-info-label">가입일</span>
                        <span class="user-info-value">${userInfo.signupDate || '알 수 없음'}</span>
                    </div>
                    ${userInfo.description ? `
                    <div class="user-info-row description">
                        <span class="user-info-label">자기소개</span>
                        <p class="user-info-value">${userInfo.description}</p>
                    </div>
                    ` : ''}
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
    document.getElementById('userInfoModal').addEventListener('click', function (e) {
        if (e.target === this) {
            closeUserInfoModal();
        }
    });

    // ESC 키로 닫기
    document.addEventListener('keydown', function handleEsc(e) {
        if (e.key === 'Escape') {
            closeUserInfoModal();
            document.removeEventListener('keydown', handleEsc);
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
