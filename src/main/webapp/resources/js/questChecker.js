/**
 * questChecker.js
 * 퀘스트 조건 체크 및 진행도 관리
 * - 유저 행동 감지 → 조건 체크 → 서버에 progress 요청
 */

const QuestChecker = (function() {
    // 활성 퀘스트 목록 (서버에서 로드)
    let activeQuests = [];

    // 컨텍스트 경로
    let contextPath = '';

    /**
     * 퀘스트별 조건 체커 정의
     * - event: 트리거 이벤트 타입
     * - check: 조건 체크 함수 (data, quest) => result | null
     */
    const QuestCheckers = {
        // d1: MiNa 여론 조사 - 검색 퀘스트
        'd1': {
            event: 'SEARCH',
            check: (keyword, quest) => {
                const targets = ['mina', '미나', '노민아'];
                const lowerKeyword = keyword.toLowerCase();
                if (targets.includes(lowerKeyword)) {
                    // objectives에서 해당 키워드와 매칭되는 미완료 목표 찾기
                    if (quest.objectives) {
                        const obj = quest.objectives.find(o =>
                            !o.completed && o.description.toLowerCase().includes(lowerKeyword)
                        );
                        if (obj) {
                            return { objectiveId: obj.id };
                        }
                    }
                }
                return null;
            }
        },

        // d3: 팬 별명 조사 - 검색 퀘스트
        'd3': {
            event: 'SEARCH',
            check: (keyword, quest) => {
                const targets = ['노미남', '밍토끼'];
                const lowerKeyword = keyword.toLowerCase();
                if (targets.some(t => t.toLowerCase() === lowerKeyword)) {
                    if (quest.objectives) {
                        const obj = quest.objectives.find(o =>
                            !o.completed && o.description.toLowerCase().includes(lowerKeyword)
                        );
                        if (obj) {
                            return { objectiveId: obj.id };
                        }
                    }
                }
                return null;
            }
        },

        // t1: MiNa 게시글 좋아요 10회
        't1': {
            event: 'LIKE',
            check: (data, quest) => {
                // MiNa 관련 게시글이면 진행
                if (data.isRelatedMina) {
                    return { increment: 1 };
                }
                return null;
            }
        },

        // t2: MiNa 게시글에 댓글 3회
        't2': {
            event: 'COMMENT',
            check: (data, quest) => {
                // data: { postAuthorId, postId }
                if (data.postAuthorId === 'mina_official' || data.postAuthorNickname === 'MiNa') {
                    return { increment: 1 };
                }
                return null;
            }
        },

        // t3: 게시글 작성 후 댓글 8개 받기 (서버에서 체크)
        't3': {
            event: 'RECEIVE_COMMENTS',
            check: (data, quest) => {
                // data: { postId, commentCount }
                if (data.commentCount >= 8) {
                    return { increment: 1 };
                }
                return null;
            }
        },

        // start: 안티팬 3명 정지 (신고 처리 결과)
        'start': {
            event: 'ANTI_BANNED',
            check: (data, quest) => {
                // data: { userId, banType }
                if (data.banType === 'PERMANENT') {
                    return { increment: 1 };
                }
                return null;
            }
        },

        // b1: 학폭 논란 - 신고 퀘스트
        'b1': {
            event: 'REPORT',
            check: (data, quest) => {
                // data: { targetType, targetId, reason }
                if (quest.objectives) {
                    // 초기 발단 게시글 신고 (objectiveId 101, 102)
                    // 악의적인 댓글 신고 (objectiveId 103)
                    const obj = quest.objectives.find(o => !o.completed);
                    if (obj) {
                        return { objectiveId: obj.id };
                    }
                }
                return null;
            }
        },

        // b2: 학폭 논란 - 댓글 달기
        'b2': {
            event: 'COMMENT',
            check: (data, quest) => {
                // 중립/옹호 댓글 (objectiveId 104, 105)
                if (quest.objectives) {
                    const obj = quest.objectives.find(o => !o.completed);
                    if (obj) {
                        return { objectiveId: obj.id };
                    }
                }
                return null;
            }
        },

        // d2: 회사 계정으로 쪽지 보내기
        'd2': {
            event: 'SEND_MESSAGE',
            check: (data, quest) => {
                // data: { recipientId }
                if (data.recipientId === 'company' || data.recipientId === 'COMPANY') {
                    if (quest.objectives) {
                        const obj = quest.objectives.find(o => !o.completed);
                        if (obj) {
                            return { objectiveId: obj.id };
                        }
                    }
                }
                return null;
            }
        }
    };

    /**
     * 초기화 - 서버에서 활성 퀘스트 로드
     */
    async function init(ctxPath) {
        contextPath = ctxPath || '';
        await loadActiveQuests();
        console.log('[QuestChecker] 초기화 완료, 활성 퀘스트:', activeQuests.length + '개');
    }

    /**
     * 활성 퀘스트 목록 로드
     */
    async function loadActiveQuests() {
        try {
            const url = contextPath + '/api/quest/questList.jsp';
            console.log('[QuestChecker] 퀘스트 로드 URL:', url);
            const res = await fetch(url);
            console.log('[QuestChecker] 응답 상태:', res.status);
            if (res.ok) {
                const data = await res.json();
                console.log('[QuestChecker] 로드된 데이터:', data);
                activeQuests = data.quests || [];
            } else {
                console.error('[QuestChecker] 응답 실패:', res.status);
            }
        } catch (e) {
            console.error('[QuestChecker] 퀘스트 로드 실패:', e);
        }
    }

    /**
     * 이벤트 발생 시 퀘스트 체크
     * @param {string} eventType - 이벤트 타입 (SEARCH, LIKE, COMMENT, etc.)
     * @param {*} data - 이벤트 데이터
     */
    async function onAction(eventType, data) {
        console.log('[QuestChecker] onAction:', eventType, data);
        for (const quest of activeQuests) {
            const checker = QuestCheckers[quest.id];
            console.log('[QuestChecker] 퀘스트 체크:', quest.id, checker ? checker.event : 'no checker');
            if (checker && checker.event === eventType) {
                const result = checker.check(data, quest);
                console.log('[QuestChecker] 체크 결과:', result);
                if (result) {
                    await progressQuest(quest.id, result);
                }
            }
        }
    }

    /**
     * 서버에 퀘스트 진행도 요청
     */
    async function progressQuest(questId, result) {
        try {
            const res = await fetch(contextPath + '/api/quest/questProgress.jsp', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ questId, ...result })
            });

            if (!res.ok) {
                console.error('[QuestChecker] 진행도 업데이트 실패');
                return;
            }

            const updated = await res.json();

            if (updated.success) {
                // 로컬 퀘스트 상태 업데이트
                updateLocalQuest(questId, updated);

                // 완료됐으면 목록에서 제거
                if (updated.status === 'COMPLETED') {
                    activeQuests = activeQuests.filter(q => q.id !== questId);

                    // 다음 퀘스트 있으면 추가
                    if (updated.nextQuest) {
                        activeQuests.push(updated.nextQuest);
                    }

                    // 완료 알림
                    showQuestComplete(updated);
                } else if (updated.status === 'COMPLETABLE') {
                    // 완료 가능 상태 알림
                    showQuestCompletable(updated);
                } else {
                    // 진행도 업데이트 알림
                    showQuestProgress(updated);
                }
            }
        } catch (e) {
            console.error('[QuestChecker] 진행도 업데이트 에러:', e);
        }
    }

    /**
     * 로컬 퀘스트 상태 업데이트
     */
    function updateLocalQuest(questId, updated) {
        const quest = activeQuests.find(q => q.id === questId);
        if (quest) {
            quest.status = updated.status;
            quest.currentProgress = updated.currentProgress;
            if (updated.objectives) {
                quest.objectives = updated.objectives;
            }
        }
    }

    /**
     * 퀘스트 진행도 토스트
     */
    function showQuestProgress(data) {
        showToast(`퀘스트 진행: ${data.title} (${data.currentProgress}/${data.requiredProgress})`, 'info');
    }

    /**
     * 퀘스트 완료 가능 토스트
     */
    function showQuestCompletable(data) {
        showToast(`퀘스트 완료 가능: ${data.title}`, 'success');
    }

    /**
     * 퀘스트 완료 토스트
     */
    function showQuestComplete(data) {
        showToast(`🎉 퀘스트 완료: ${data.title}`, 'success');
        if (data.rewardReputation > 0) {
            showToast(`평판 +${data.rewardReputation}`, 'reward');
        }
    }

    /**
     * 토스트 메시지 표시
     */
    function showToast(message, type) {
        // 기존 토스트 컨테이너 찾거나 생성
        let container = document.getElementById('quest-toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'quest-toast-container';
            container.style.cssText = `
                position: fixed;
                bottom: 30px;
                right: 30px;
                z-index: 9999;
                display: flex;
                flex-direction: column;
                gap: 8px;
                align-items: flex-end;
            `;
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = 'quest-toast quest-toast-' + type;
        toast.style.cssText = `
            padding: 12px 20px;
            border-radius: 8px;
            color: white;
            font-size: 14px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.3);
            animation: slideIn 0.3s ease;
            max-width: 300px;
        `;

        // 타입별 배경색
        const colors = {
            info: '#4a90d9',
            success: '#4CAF50',
            reward: '#FFD700',
            error: '#f44336'
        };
        toast.style.backgroundColor = colors[type] || colors.info;
        if (type === 'reward') {
            toast.style.color = '#333';
        }

        toast.textContent = message;
        container.appendChild(toast);

        // 5초 후 제거
        setTimeout(() => {
            toast.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        }, 5000);
    }

    // CSS 애니메이션 추가
    const style = document.createElement('style');
    style.textContent = `
        @keyframes slideIn {
            from { transform: translateY(20px); opacity: 0; }
            to { transform: translateY(0); opacity: 1; }
        }
        @keyframes slideOut {
            from { transform: translateY(0); opacity: 1; }
            to { transform: translateY(20px); opacity: 0; }
        }
    `;
    document.head.appendChild(style);

    // === 이벤트별 헬퍼 함수 ===

    /**
     * 검색 이벤트
     */
    async function onSearch(keyword) {
        if (keyword && keyword.trim()) {
            console.log('[QuestChecker] 검색 이벤트:', keyword.trim());
            console.log('[QuestChecker] 활성 퀘스트:', activeQuests);
            await onAction('SEARCH', keyword.trim());
        }
    }

    /**
     * 좋아요 이벤트
     */
    function onLike(data) {
        // data: { authorId, authorNickname, postId }
        onAction('LIKE', data);
    }

    /**
     * 댓글 작성 이벤트
     */
    function onComment(data) {
        // data: { postAuthorId, postAuthorNickname, postId }
        onAction('COMMENT', data);
    }

    /**
     * 신고 이벤트
     */
    function onReport(data) {
        // data: { targetType, targetId, reason }
        onAction('REPORT', data);
    }

    /**
     * 쪽지 전송 이벤트
     */
    function onSendMessage(data) {
        // data: { recipientId }
        onAction('SEND_MESSAGE', data);
    }

    /**
     * 현재 활성 퀘스트 반환
     */
    function getActiveQuests() {
        return [...activeQuests];
    }

    /**
     * 활성 퀘스트 수동 설정 (SSR용)
     */
    function setActiveQuests(quests) {
        activeQuests = quests || [];
    }

    // Public API
    return {
        init,
        loadActiveQuests,
        onAction,
        onSearch,
        onLike,
        onComment,
        onReport,
        onSendMessage,
        getActiveQuests,
        setActiveQuests,
        showToast
    };
})();

// 전역 노출
window.QuestChecker = QuestChecker;
