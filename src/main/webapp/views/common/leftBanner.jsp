<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.GameContext" %>

<jsp:useBean id="gameContext" class="model.GameContext" scope="session" />

<%
    String contextPath = request.getContextPath();
%>

<!-- 왼쪽 고정 사이드 배너 -->
<div class="left-fixed-banner">
    <div class="banner-image-area">
        <img src="<%= contextPath %>/resources/image/edited.png" alt="배너" class="banner-image">
    </div>
    <div class="banner-dday-area">
        <span class="banner-dday-label">앨범 발매</span>
        <span class="banner-dday-value"><%= gameContext.getGameState().getDDayText() %></span>
    </div>
</div>

<style>
/* 왼쪽 고정 사이드 배너 스타일 */
.left-fixed-banner {
    position: fixed;
    left: 10px;
    top: 100px;
    width: 160px;
    background: #000;
    overflow: hidden;
    z-index: 100;
    box-shadow: 2px 4px 4px rgba(0, 0, 0, 0.3);
}

.banner-image-area {
    width: 100%;
}

.banner-image {
    width: 160px;
    height: auto;
    display: block;
    object-fit: cover;
    object-position: top;
}

.banner-dday-area {
    padding: 15px 10px;
    text-align: center;
    background: #000;
    border-top: 1px solid #333;
}

.banner-dday-label {
    display: block;
    font-size: 15px;
    color: #aaa;
    margin-bottom: 5px;
}

.banner-dday-value {
    display: block;
    font-size: 22px;
    font-weight: bold;
    color: #fff;
    letter-spacing: 1px;
}

/* 반응형: 화면이 작아지면 배너 숨김 */
@media (max-width: 1400px) {
    .left-fixed-banner {
        display: none;
    }
}
</style>
