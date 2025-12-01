<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.UUID" %>
<%@ page import="manager.DataManager" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- GameContext 초기화 확인 --%>
<jsp:useBean id="gameContext" class="model.GameContext" scope="session" />
<%
    // GameContext가 초기화되지 않았다면 초기화
    if (gameContext.getGameState() == null) {
        String pid = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("pid".equals(cookie.getName())) {
                    pid = cookie.getValue();
                    break;
                }
            }
        }

        // pid가 없으면 index.jsp로 리다이렉트
        if (pid == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        gameContext.init(pid, DataManager.getInstance());
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>케이돌 토크 - 더꾸</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css?v=2">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/board.css">
</head>
<body>
    <!-- 헤더 include -->
    <jsp:include page="../common/header.jsp">
        <jsp:param name="page" value="kdolTalk" />
    </jsp:include>

    <!-- 메인 컨텐츠 영역 -->
    <div class="xe">
        <div class="xe_width content-wrapper">
            <!-- 게시판 영역 -->
            <div class="board-area">
                <!-- 게시판 상단 -->
                <div class="board-header">
                    <h2>케이돌 토크</h2>
                    <div class="board-category">
                        <a href="?category=all" class="active">전체</a>
                        <a href="?category=chat">잡담</a>
                        <a href="?category=square">스퀘어</a>
                        <a href="?category=notice">알림/결과</a>
                        <a href="?category=review">후기</a>
                        <a href="?category=onair">onair</a>
                    </div>
                </div>

                <!-- 검색 바 -->
                <jsp:include page="../common/searchBar.jsp">
                    <jsp:param name="action" value="${pageContext.request.contextPath}/board/search" />
                    <jsp:param name="placeholder" value="검색어를 입력하세요" />
                </jsp:include>

                <!-- 게시판 목록 -->
                <div class="board-list">
                    <table>
                        <thead>
                            <tr>
                                <th class="col-no">번호</th>
                                <th class="col-category">카테고리</th>
                                <th class="col-title">제목</th>
                                <th class="col-author">작성자</th>
                                <th class="col-date">날짜</th>
                                <th class="col-like">추천</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                // 실제 게시글 데이터 가져오기
                                java.util.List<dto.Post> posts = gameContext.getPostManager().getAllPosts();

                                // 카테고리 필터링
                                String categoryParam = request.getParameter("category");
                                if (categoryParam != null && !"all".equals(categoryParam)) {
                                    posts = posts.stream()
                                        .filter(p -> categoryParam.equals(p.getCategory()))
                                        .collect(java.util.stream.Collectors.toList());
                                }

                                // 최신순 정렬
                                posts.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));

                                // 날짜 포맷터
                                java.time.format.DateTimeFormatter dateFormatter =
                                    java.time.format.DateTimeFormatter.ofPattern("MM.dd HH:mm");

                                int postNumber = posts.size();
                                for (dto.Post post : posts) {
                                    String formattedDate = post.getCreatedAt().format(dateFormatter);

                                    // 댓글 개수 가져오기
                                    int commentCount = gameContext.getPostManager().getCommentsByPostId(post.getPostId()).size();

                                    // 닉네임 처리 (JSON에 없으면 NPC ID로부터 생성)
                                    String nickname = post.getAuthorNickname();
                                    if (nickname == null || nickname.isEmpty()) {
                                        nickname = gameContext.getPostManager().assignNicknameForNPC(
                                            post.getAuthorPid(),
                                            post.getPostId()
                                        );
                                    }
                            %>
                            <tr>
                                <td class="col-no"><%= postNumber-- %></td>
                                <td class="col-category"><%= post.getCategory() != null ? post.getCategory() : "잡담" %></td>
                                <td class="col-title">
                                    <a href="${pageContext.request.contextPath}/views/board/postView.jsp?id=<%= post.getPostId() %>">
                                        <%= post.getTitle() %>
                                    </a>
                                    <% if (commentCount > 0) { %>
                                        <span class="comment-count">[<%= commentCount %>]</span>
                                    <% } %>
                                    <% if (post.isHasPictures()) { %>
                                        <span class="icon-picture">📷</span>
                                    <% } %>
                                </td>
                                <td class="col-author"><%= nickname %></td>
                                <td class="col-date"><%= formattedDate %></td>
                                <td class="col-like"><%= post.getLikeCount() %></td>
                            </tr>
                            <%
                                }

                                if (posts.isEmpty()) {
                            %>
                            <tr>
                                <td colspan="6" style="text-align: center; padding: 50px;">
                                    게시글이 없습니다.
                                </td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                </div>

                <!-- 페이지네이션 -->
                <jsp:include page="../common/pagination.jsp">
                    <jsp:param name="currentPage" value="4" />
                    <jsp:param name="totalPages" value="100" />
                </jsp:include>

                <!-- 글쓰기 버튼 -->
                <jsp:include page="../common/boardActions.jsp">
                    <jsp:param name="href" value="${pageContext.request.contextPath}/views/board/postWrite.jsp" />
                    <jsp:param name="buttonText" value="글쓰기" />
                </jsp:include>
            </div>

            <!-- 사이드바 영역 -->
            <%
                // 쪽지함 표시 여부 확인 (기본값: 닫힌 상태)
                String showMessages = request.getParameter("showMessages");
                boolean isMessageVisible = "true".equals(showMessages);
            %>
            <div class="sidebar-area <%= !isMessageVisible ? "hidden" : "" %>">
                <jsp:include page="../common/messageSidebar.jsp" />
            </div>
        </div>
    </div>

    <!-- 푸터 include -->
    <jsp:include page="../common/footer.jsp" />
</body>
</html>