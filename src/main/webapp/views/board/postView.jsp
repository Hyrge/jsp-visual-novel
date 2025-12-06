<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="service.PostService" %>
<%@ page import="dto.Post" %>
<%@ page import="dto.Comment" %>
<%@ page import="model.GameContext" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="gameContext" class="model.GameContext" scope="session" />
<jsp:useBean id="player" class="dto.User" scope="session" />

<%
	request.setCharacterEncoding("UTF-8");
    String pid = gameContext.getPid();
    if (pid == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    
    String contextPath = request.getContextPath();

    // 댓글 작성 처리 (POST 요청)
    if ("POST".equalsIgnoreCase(request.getMethod())) {

        String postId = request.getParameter("postId");
        String commentContent = request.getParameter("commentContent");
        String replyContent = request.getParameter("replyContent");
        String parentCommentIdStr = request.getParameter("parentCommentId");

        String content = (commentContent != null && !commentContent.trim().isEmpty())
                        ? commentContent.trim()
                        : (replyContent != null ? replyContent.trim() : null);

        if (postId == null || content == null || content.isEmpty()) {
            out.println("<script>alert('댓글 내용을 입력해주세요.'); history.back();</script>");
            return;
        }

        // 대댓글인 경우 parentCommentId 파싱
        Integer parentCommentId = null;
        if (parentCommentIdStr != null && !parentCommentIdStr.trim().isEmpty()) {
            try {
                parentCommentId = Integer.parseInt(parentCommentIdStr.trim());
            } catch (NumberFormatException e) {
                // 파싱 실패 시 null 유지 (일반 댓글로 처리)
            }
        }

        LocalDateTime currentDateTime = gameContext.getGameState().getCurrentDateTime();

        // Comment 객체 생성
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setNickname(player.getNickname());
        comment.setPlayerPid(pid);
        comment.setContent(content);
        comment.setCreatedAt(currentDateTime);
        comment.setParentCommentId(parentCommentId); 

        // PostService를 통해 DB에 저장
        PostService postService = gameContext != null ? gameContext.getPostService() : new service.PostService(manager.DataManager.getInstance());
        boolean success = postService.createComment(comment);

        if (success) {
            // 게임 시간 1분 진행
            if (gameContext != null) {
                gameContext.getGameState().advanceTime(3); // 댓글 작성 시 3분 경과
            }

            // 같은 게시글 페이지로 리다이렉트
            response.sendRedirect(request.getContextPath() + "/views/board/postView.jsp?id=" + postId);
            return;
        } else {
            out.println("<script>alert('댓글 저장에 실패했습니다.'); history.back();</script>");
            return;
        }
    }

    // GET 요청 처리 (게시글 조회)
    String postId = request.getParameter("id");
    if (postId == null || postId.trim().isEmpty()) {
        response.sendRedirect(request.getContextPath() + "/views/board/kdolTalkBoard.jsp");
        return;
    }

    // PostService에서 게시글 데이터 가져오기
    PostService postService = gameContext.getPostService();
    Post post = postService.getPost(postId);

    if (post == null) {
        out.println("<script>alert('게시글을 찾을 수 없습니다.'); location.href='" + request.getContextPath() + "/views/board/kdolTalkBoard.jsp';</script>");
        return;
    }

    // 댓글 목록 가져오기 (playerPid로 필터링)
    List<Comment> commentList = postService.getComments(postId, pid, gameContext.getGameState().getCurrentDateTime());

    // JSP에서 사용할 데이터 변환
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    Map<String, Object> postData = new HashMap<>();
    postData.put("id", post.getPostId());
    postData.put("category", post.getCategory());
    postData.put("title", post.getTitle());
    postData.put("nickname", post.getNickname() != null ? post.getNickname() : "유저");
    postData.put("playerPid", post.getPlayerPid()); // 이미지 경로용
    postData.put("date", post.getCreatedAt().format(dateFormatter));
    postData.put("views", "0"); // TODO: 조회수 기능 추가
    postData.put("likes", String.valueOf(post.getLikeCount()));
    postData.put("dislikes", String.valueOf(post.getDislikeCount()));
    postData.put("content", post.getContent().replace("\n", "<br>"));
    postData.put("imageFile", post.getImageFile()); // 이미지 파일명
    postData.put("hasPictures", post.isHasPictures()); // 이미지 유무

    request.setAttribute("post", postData);

    // 댓글 데이터 변환
    List<Map<String, String>> commentsData = new ArrayList<>();
    for (Comment c : commentList) {
        Map<String, String> commentMap = new HashMap<>();
        commentMap.put("comment_id", String.valueOf(c.getCommentId()));
        commentMap.put("player_id", c.getPlayerPid());
        commentMap.put("nickname", c.getNickname() != null ? c.getNickname() : "유저");
        commentMap.put("parent_comment_id", c.getParentCommentId() != null ? String.valueOf(c.getParentCommentId()) : "");
        commentMap.put("date", c.getCreatedAt().format(dateFormatter));
        commentMap.put("content", c.getContent() != null ? c.getContent().replace("\n", "<br>") : "");
        commentsData.add(commentMap);
    }

    request.setAttribute("comments", commentsData);
    request.setAttribute("commentCount", commentsData.size());
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${post.title} - 케이돌 토크 - 더꾸</title>
    <link rel="stylesheet" href="<%= contextPath %>/resources/css/style.css?v=2">
    <link rel="stylesheet" href="<%= contextPath %>/resources/css/board.css">
    <link rel="stylesheet" href="<%= contextPath %>/resources/css/postView.css">
    <link rel="stylesheet" href="<%= contextPath %>/resources/css/userTooltip.css">
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
                        <span class="user-tooltip-wrapper">
                            <span class="author post-author-link">${post.author}</span>
                            <div class="user-tooltip">
                                <a href="javascript:void(0)" class="user-tooltip-item" onclick="viewUserInfo('${post.author}')">회원정보</a>
                                <a href="javascript:void(0)" class="user-tooltip-item" onclick="sendMessage('${post.author}')">쪽지 보내기</a>
                            </div>
                        </span>
                        <span class="separator">|</span>
                        <span class="date">${post.date}</span>
                        <span class="separator">|</span>
                        <span class="views">조회 ${post.views}</span>
                    </div>
                </div>

                <!-- 게시글 본문 -->
                <div class="post-content">
                    <!-- 이미지 표시 -->
                    <c:if test="${post.hasPictures && post.imageFile != null}">
                    <div class="post-image">
                        <img src="<%= contextPath %>/saves/${post.playerPid}/${post.imageFile}" alt="첨부 이미지" onclick="openImageModal(this.src)">
                    </div>
                    </c:if>
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
                    <a href="<%= contextPath %>/views/board/kdolTalkBoard.jsp" class="btn btn-list">목록</a>
                    <div class="right-buttons">
                        <button type="button" class="btn btn-report" onclick="reportPost('${post.id}')">신고</button>
                        <!-- 본인 글인 경우만 표시 -->
                        <%--
                        <a href="<%= contextPath %>/views/board/postEdit.jsp?id=${post.id}" class="btn btn-edit">수정</a>
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
                        <form id="commentForm" method="post" action="<%= contextPath %>/views/board/postView.jsp" onsubmit="return validateComment()">
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
                        <div class="comment-item${not empty comment.parent_comment_id && comment.parent_comment_id != '' ? ' reply-item' : ''}" id="${comment.comment_id}" data-parent="${comment.parent_comment_id}">
                            <div class="comment-header-row">
                                <div class="comment-author-info">
                                    <span class="user-tooltip-wrapper comment-nickname-wrapper">
                                        <span class="comment-author" onclick="toggleReplyForm('${comment.comment_id}', '${comment.nickname}')" style="cursor: pointer;">${comment.nickname}</span>
                                        <div class="user-tooltip">
                                            <a href="javascript:void(0)" class="user-tooltip-item" onclick="viewUserInfo('${comment.nickname}')">회원정보</a>
                                            <a href="javascript:void(0)" class="user-tooltip-item" onclick="sendMessage('${comment.nickname}')">쪽지 보내기</a>
                                        </div>
                                    </span>
                                    <span class="comment-date">${comment.date}</span>
                                </div>
                                <button type="button" class="btn-comment-report" onclick="reportComment('${comment.comment_id}')">
                                    신고
                                </button>
                            </div>
                            <div class="comment-content">
                                ${comment.content}
                            </div>

                            <!-- 답글 작성 폼 (닉네임 클릭 시 나타남) -->
                            <div class="reply-form-section" id="replyForm-${comment.comment_id}" style="display: none;">
                                <form method="post" action="<%= contextPath %>/views/board/postView.jsp" onsubmit="return validateReply('${comment.comment_id}')">
                                    <input type="hidden" name="postId" value="${post.id}">
                                    <input type="hidden" name="parentCommentId" value="${comment.comment_id}">
                                    <textarea name="replyContent" id="replyContent-${comment.comment_id}" placeholder="답글을 입력하세요..." rows="2" maxlength="500"></textarea>
                                    <div class="reply-write-bottom">
                                        <span class="reply-char-count"><span id="replyLength-${comment.comment_id}">0</span>/500</span>
                                        <div class="reply-buttons">
                                            <button type="button" class="btn btn-cancel" onclick="closeReplyForm('${comment.comment_id}')">취소</button>
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

    <!-- 이미지 모달 -->
    <div id="imageModal" class="image-modal" onclick="closeImageModal()">
        <span class="modal-close">&times;</span>
        <img id="modalImage" class="modal-content">
    </div>

    <!-- 푸터 include -->
    <jsp:include page="../common/footer.jsp" />

    <!-- JavaScript 파일들 -->
    <script>
        // 이미지 모달 열기
        function openImageModal(src) {
            var modal = document.getElementById('imageModal');
            var modalImg = document.getElementById('modalImage');
            modal.style.display = 'flex';
            modalImg.src = src;
        }
        
        // 이미지 모달 닫기
        function closeImageModal() {
            var modal = document.getElementById('imageModal');
            modal.style.display = 'none';
        }
        
        // ESC 키로 모달 닫기
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                closeImageModal();
            }
        });
    </script>
    <script src="<%= contextPath %>/resources/js/validation.js"></script>
    <script src="<%= contextPath %>/resources/js/comment.js"></script>
    <script src="<%= contextPath %>/resources/js/post.js"></script>
    <script src="<%= contextPath %>/resources/js/charCounter.js"></script>
    <script src="<%= contextPath %>/resources/js/userTooltip.js"></script>
</body>
</html>
