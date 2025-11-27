<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>케이돌 토크 - 더꾸</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
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
                                <th class="col-view">조회</th>
                                <th class="col-like">추천</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- 공지사항 예시 -->
                            <tr class="notice">
                                <td class="col-no"><span class="badge-notice">공지</span></td>
                                <td class="col-category">공지</td>
                                <td class="col-title">
                                    <a href="#">[필독] 케이돌 토크 게시판 이용 규칙</a>
                                </td>
                                <td class="col-author">운영자</td>
                                <td class="col-date">2025.11.27</td>
                                <td class="col-view">1,234</td>
                                <td class="col-like">56</td>
                            </tr>

                            <!-- 일반 게시글 예시 -->
                            <tr>
                                <td class="col-no">150</td>
                                <td class="col-category">잡담</td>
                                <td class="col-title">
                                    <a href="#">MiNa 신곡 너무 좋은데?</a>
                                    <span class="comment-count">[12]</span>
                                </td>
                                <td class="col-author">user123</td>
                                <td class="col-date">11:23</td>
                                <td class="col-view">345</td>
                                <td class="col-like">28</td>
                            </tr>

                            <tr>
                                <td class="col-no">149</td>
                                <td class="col-category">후기</td>
                                <td class="col-title">
                                    <a href="#">어제 팬미팅 다녀왔어요 후기</a>
                                    <span class="comment-count">[45]</span>
                                </td>
                                <td class="col-author">mina_fan</td>
                                <td class="col-date">10:15</td>
                                <td class="col-view">892</td>
                                <td class="col-like">67</td>
                            </tr>

                            <tr>
                                <td class="col-no">148</td>
                                <td class="col-category">onair</td>
                                <td class="col-title">
                                    <a href="#">지금 인스타 라이브 중!</a>
                                    <span class="comment-count">[8]</span>
                                </td>
                                <td class="col-author">realtime_kr</td>
                                <td class="col-date">09:45</td>
                                <td class="col-view">567</td>
                                <td class="col-like">89</td>
                            </tr>
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
                    <jsp:param name="href" value="${pageContext.request.contextPath}/board/write" />
                    <jsp:param name="buttonText" value="글쓰기" />
                </jsp:include>
            </div>

            <!-- 사이드바 영역 -->
            <div class="sidebar-area">
                <jsp:include page="../common/messageSidebar.jsp" />
            </div>
        </div>
    </div>

    <!-- 푸터 include -->
    <jsp:include page="../common/footer.jsp" />
</body>
</html>