<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
                <!-- 검색 바 -->
                <div class="board_search_bar">
                    <form action="kdolTalkBoard.jsp" method="get" onsubmit="return onSearchSubmit(event)">
                        <input type="text" id="board_search" name="keyword" placeholder="${param.placeholder}" value="${param.keyword}">
                        <button type="submit" class="btn-search">검색</button>
                    </form>
                </div>
                <script>
                    async function onSearchSubmit(event) {
                        const keyword = document.getElementById('board_search').value;
                        if (keyword && keyword.trim() && window.QuestChecker) {
                            // 폼 제출 잠시 대기하고 퀘스트 체크 먼저 실행
                            event.preventDefault();
                            await QuestChecker.onSearch(keyword.trim());
                            // 퀘스트 체크 후 폼 제출
                            event.target.submit();
                        }
                        return true;
                    }
                </script>

