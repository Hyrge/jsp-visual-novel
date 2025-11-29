<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // 쪽지함 상태 파라미터 확인
    String showMessages = request.getParameter("showMessages");
    String messageParam = (showMessages != null) ? "?showMessages=" + showMessages : "";
%>
<!-- 헤더 영역 -->
<header class="header">
    <div class="xe_width clearBoth">
        <h1><a href="${pageContext.request.contextPath}/">더꾸</a></h1>

        <!-- GNB 네비게이션 -->
        <nav class="gnb">
            <ul class="navbar-nav">
                <li class="dropdown">
                    <a href="#" class="first_a">전체</a>
                </li>
                <li class="dropdown">
                    <a href="#" class="first_a">HOT</a>
                </li>
                <li class="dropdown">
                    <a href="#" class="first_a">스퀘어</a>
                </li>
                <li class="dropdown">
                    <a href="#" class="first_a">뷰티</a>
                </li>
                <li class="dropdown">
                    <a href="#" class="first_a">일상토크</a>
                </li>
                <li class="dropdown ${param.page == 'kdolTalk' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/views/board/kdolTalkBoard.jsp<%= messageParam %>" class="first_a">케이돌토크</a>
                </li>
                <li class="dropdown ${param.page == 'report' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/views/board/reportBoard.jsp<%= messageParam %>" class="first_a">신고</a>
                </li>
            </ul>
        </nav>

        <!-- 우측 메뉴 -->
        <div class="navbar-right">
            <a href="#" class="first_a" id="message-toggle-btn" onclick="toggleMessageSidebar(); return false;">
                <img src="${pageContext.request.contextPath}/resources/image/message.png" alt="쪽지" style="height: 20px; vertical-align: middle;">
            </a>
            <a href="${pageContext.request.contextPath}/" class="first_a">로그아웃</a>
        </div>
    </div>
</header>