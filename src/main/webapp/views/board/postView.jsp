<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*, java.text.SimpleDateFormat" %>
<%
    // 게시글 ID 파라미터 받기
    String postId = request.getParameter("id");

    // TODO: 실제로는 DB나 JSON에서 게시글 데이터 가져오기
    // 현재는 예시 데이터 사용

    // 게시글 데이터 (임시)
    Map<String, String> post = new HashMap<>();
    post.put("id", postId != null ? postId : "150");
    post.put("category", "잡담");
    post.put("title", "MiNa 신곡 너무 좋은데?");
    post.put("author", "user123");
    post.put("date", "2025-11-27 11:23");
    post.put("views", "345");
    post.put("likes", "28");
    post.put("dislikes", "3");
    post.put("content", "어제 공개된 MiNa 신곡 들어봤는데 진짜 대박이에요!<br><br>" +
                       "특히 후렴구 부분이 너무 중독적이고, 뮤직비디오 퀄리티도 장난 아님ㅋㅋ<br><br>" +
                       "이번에는 진짜 음원차트 1위 가능할 것 같은데 다들 어떻게 생각하시나요?<br><br>" +
                       "댓글로 의견 남겨주세요!");

    request.setAttribute("post", post);

    // 댓글 데이터 (임시)
    List<Map<String, String>> comments = new ArrayList<>();

    Map<String, String> comment1 = new HashMap<>();
    comment1.put("id", "1");
    comment1.put("author", "mina_lover");
    comment1.put("date", "2025-11-27 11:30");
    comment1.put("content", "인정요!! 저도 계속 반복재생 중ㅋㅋ");
    comment1.put("likes", "5");
    comment1.put("dislikes", "0");
    comments.add(comment1);

    Map<String, String> comment2 = new HashMap<>();
    comment2.put("id", "2");
    comment2.put("author", "kdol_fan");
    comment2.put("date", "2025-11-27 11:45");
    comment2.put("content", "뮤비 진짜 예술이더라... 이번에는 대박날듯");
    comment2.put("likes", "8");
    comment2.put("dislikes", "1");
    comments.add(comment2);

    Map<String, String> comment3 = new HashMap<>();
    comment3.put("id", "3");
    comment3.put("author", "hater123");
    comment3.put("date", "2025-11-27 12:10");
    comment3.put("content", "별로인데? 과대평가ㅋㅋ");
    comment3.put("likes", "2");
    comment3.put("dislikes", "15");
    comments.add(comment3);

    request.setAttribute("comments", comments);
    request.setAttribute("commentCount", comments.size());
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${post.title} - 케이돌 토크 - 더꾸</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css?v=2">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/board.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/postView.css">
</head>
<body>
    <!-- 헤더 include -->
    <jsp:include page="../common/header.jsp">
        <jsp:param name="page" value="kdolTalk" />
    </jsp:include>

    <!-- 메인 컨텐츠 영역 -->
    <div class="xe">
        <div class="xe_width content-wrapper">
            <!-- 게시글 영역 -->
            <div class="board-area">
                <!-- 게시글 상단 정보 -->
                <div class="post-header">
                    <div class="post-category">
                        <span class="category-badge">${post.category}</span>
                    </div>
                    <h2 class="post-title">${post.title}</h2>
                    <div class="post-meta">
                        <span class="author">${post.author}</span>
                        <span class="separator">|</span>
                        <span class="date">${post.date}</span>
                        <span class="separator">|</span>
                        <span class="views">조회 ${post.views}</span>
                    </div>
                </div>

                <!-- 게시글 본문 -->
                <div class="post-content">
                    ${post.content}
                </div>

                <!-- 게시글 추천/비추천 -->
                <div class="post-actions">
                    <button type="button" class="btn-vote btn-like" onclick="votePost('${post.id}', 'like')">
                        <span class="vote-icon">👍</span>
                        <span class="vote-text">추천</span>
                        <span class="vote-count">${post.likes}</span>
                    </button>
                    <button type="button" class="btn-vote btn-dislike" onclick="votePost('${post.id}', 'dislike')">
                        <span class="vote-icon">👎</span>
                        <span class="vote-text">비추천</span>
                        <span class="vote-count">${post.dislikes}</span>
                    </button>
                </div>

                <!-- 게시글 하단 버튼 -->
                <div class="post-bottom-actions">
                    <a href="${pageContext.request.contextPath}/views/board/kdolTalkBoard.jsp" class="btn btn-list">목록</a>
                    <div class="right-buttons">
                        <button type="button" class="btn btn-report" onclick="reportPost('${post.id}')">신고</button>
                        <!-- 본인 글인 경우만 표시 -->
                        <%--
                        <a href="${pageContext.request.contextPath}/views/board/postEdit.jsp?id=${post.id}" class="btn btn-edit">수정</a>
                        <button type="button" class="btn btn-delete" onclick="deletePost('${post.id}')">삭제</button>
                        --%>
                    </div>
                </div>

                <!-- 댓글 영역 -->
                <div class="comment-section">
                    <div class="comment-header">
                        <h3>댓글 <span class="comment-count">${commentCount}</span></h3>
                    </div>

                    <!-- 댓글 작성 폼 -->
                    <div class="comment-write-section">
                        <form id="commentForm" method="post" action="${pageContext.request.contextPath}/board/addComment" onsubmit="return validateComment()">
                            <input type="hidden" name="postId" value="${post.id}">
                            <textarea name="commentContent" id="commentContent" placeholder="댓글을 입력하세요..." rows="3" maxlength="500"></textarea>
                            <div class="comment-write-bottom">
                                <span class="char-count"><span id="currentLength">0</span>/500</span>
                                <button type="submit" class="btn btn-comment-submit">댓글 작성</button>
                            </div>
                        </form>
                    </div>

                    <!-- 댓글 목록 -->
                    <div class="comment-list">
                        <c:forEach var="comment" items="${comments}">
                        <div class="comment-item" id="comment-${comment.id}">
                            <div class="comment-header-row">
                                <div class="comment-author-info">
                                    <span class="comment-author" onclick="toggleReplyForm('${comment.id}', '${comment.author}')" style="cursor: pointer;">${comment.author}</span>
                                    <span class="comment-date">${comment.date}</span>
                                </div>
                                <button type="button" class="btn-comment-report" onclick="reportComment('${comment.id}')">
                                    신고
                                </button>
                            </div>
                            <div class="comment-content">
                                ${comment.content}
                            </div>

                            <!-- 답글 작성 폼 (닉네임 클릭 시 나타남) -->
                            <div class="reply-form-section" id="replyForm-${comment.id}" style="display: none;">
                                <form method="post" action="${pageContext.request.contextPath}/board/addComment" onsubmit="return validateReply('${comment.id}')">
                                    <input type="hidden" name="postId" value="${post.id}">
                                    <textarea name="replyContent" id="replyContent-${comment.id}" placeholder="답글을 입력하세요..." rows="2" maxlength="500"></textarea>
                                    <div class="reply-write-bottom">
                                        <span class="reply-char-count"><span id="replyLength-${comment.id}">0</span>/500</span>
                                        <div class="reply-buttons">
                                            <button type="button" class="btn btn-cancel" onclick="closeReplyForm('${comment.id}')">취소</button>
                                            <button type="submit" class="btn btn-reply-submit">답글 작성</button>
                                        </div>
                                    </div>
                                </form>
                            </div>

                            <!-- 본인 댓글인 경우만 표시 -->
                            <%--
                            <div class="comment-actions">
                                <button type="button" class="btn-comment-delete" onclick="deleteComment('${comment.id}')">삭제</button>
                            </div>
                            --%>
                        </div>
                        </c:forEach>

                        <!-- 댓글이 없는 경우 -->
                        <c:if test="${commentCount == 0}">
                        <div class="no-comments">
                            <p>첫 번째 댓글을 작성해보세요!</p>
                        </div>
                        </c:if>
                    </div>
                </div>
            </div>

            <!-- 사이드바 영역 -->
            <%
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

    <!-- JavaScript 파일들 -->
    <script>
        // contextPath를 전역 변수로 설정 (외부 JS 파일에서 사용)
        var contextPath = '${pageContext.request.contextPath}';
    </script>
    <script src="${pageContext.request.contextPath}/resources/js/validation.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/comment.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/post.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/charCounter.js"></script>
</body>
</html>
