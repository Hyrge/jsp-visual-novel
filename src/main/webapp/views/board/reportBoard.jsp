<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>신고 게시판 - 더꾸</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/board.css">
</head>
<body>
    <!-- 헤더 include -->
    <jsp:include page="../common/header.jsp">
        <jsp:param name="page" value="report" />
    </jsp:include>

    <!-- 메인 컨텐츠 영역 -->
    <div class="xe">
        <div class="xe_width">
            <!-- 게시판 상단 -->
            <div class="board-header">
                <h2>신고 게시판</h2>
                <!-- 신고 게시판에는 카테고리가 필요 없을 수 있으므로 제거 -->
            </div>

            <!-- 검색 바 -->
            <jsp:include page="../common/searchBar.jsp">
                <jsp:param name="action" value="${pageContext.request.contextPath}/board/report/search" />
                <jsp:param name="placeholder" value="검색어를 입력하세요" />
            </jsp:include>

            <!-- 게시판 목록 -->
            <div class="board-list">
                <table>
                    <thead>
                        <tr>
                            <th class="col-no">번호</th>
                            <th class="col-category">신고 유형</th>
                            <th class="col-title">신고 제목</th>
                            <th class="col-author">신고자</th>
                            <th class="col-date">신고일</th>
                            <th class="col-view">처리 상태</th>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- 신고 목록이 들어갈 자리 -->
                        <tr>
                            <td colspan="6">등록된 신고가 없습니다.</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- 페이지네이션 -->
            <jsp:include page="../common/pagination.jsp" />

            <!-- 신고하기 버튼 -->
            <jsp:include page="../common/boardActions.jsp">
                <jsp:param name="href" value="${pageContext.request.contextPath}/board/report/write" />
                <jsp:param name="buttonText" value="글쓰기" />
            </jsp:include>
        </div>
    </div>

    <!-- 푸터 include -->
    <jsp:include page="../common/footer.jsp" />
</body>
</html>