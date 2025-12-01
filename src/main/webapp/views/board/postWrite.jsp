<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>글쓰기 - 더꾸</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css?v=2">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/board.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/postWrite.css">
</head>
<body>
    <!-- 헤더 include -->
    <jsp:include page="../common/header.jsp">
        <jsp:param name="page" value="kdolTalk" />
    </jsp:include>

    <!-- 메인 컨텐츠 영역 -->
    <div class="xe">
        <div class="xe_width content-wrapper">
            <!-- 글쓰기 영역 -->
            <div class="write-area">
                <div class="write-header">
                    <h2>게시글 작성</h2>
                </div>

                <form name="postForm" action="${pageContext.request.contextPath}/views/board/postWrite.jsp" method="post" onsubmit="return validatePost()">
                    <!-- 카테고리 선택 -->
                    <div class="form-group">
                        <label for="postCategory" class="form-label">카테고리 <span class="required">*</span></label>
                        <select id="postCategory" name="category" class="form-select" required>
                            <option value="">카테고리를 선택하세요</option>
                            <option value="chat">잡담</option>
                            <option value="square">스퀘어</option>
                            <option value="notice">알림/결과</option>
                            <option value="review">후기</option>
                            <option value="onair">onair</option>
                        </select>
                    </div>

                    <!-- 제목 입력 -->
                    <div class="form-group">
                        <label for="postTitle" class="form-label">제목 <span class="required">*</span></label>
                        <input type="text" id="postTitle" name="title" class="form-input" placeholder="제목을 입력하세요 (최대 100자)" maxlength="100" required>
                        <div class="char-counter">
                            <span id="titleLength">0</span> / 100
                        </div>
                    </div>

                    <!-- 내용 입력 -->
                    <div class="form-group">
                        <label for="postContent" class="form-label">내용 <span class="required">*</span></label>
                        <textarea id="postContent" name="content" class="form-textarea" placeholder="내용을 입력하세요 (최대 5000자)" maxlength="5000" rows="15" required></textarea>
                        <div class="char-counter">
                            <span id="contentLength">0</span> / 5000
                        </div>
                    </div>

                    <!-- 버튼 그룹 -->
                    <div class="button-group">
                        <button type="button" class="btn-cancel" onclick="history.back()">취소</button>
                        <button type="submit" class="btn-submit">작성 완료</button>
                    </div>
                </form>
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

    <!-- JavaScript -->
    <script src="${pageContext.request.contextPath}/resources/js/validation.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/charCounter.js"></script>
</body>
</html>
