<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- 왼쪽 고정 사이드 배너 -->
<jsp:include page="leftBanner.jsp" />

<!-- 푸터 영역 -->
<div class="footer">
    <div class="xe_width clearBoth">
        <div class="footer_left">
            <img src="${pageContext.request.contextPath}/resources/images/logo.png" alt="더꾸">
        </div>
        <div class="footer_right">
            <ul class="clearBoth">
                <li class="footer_li">
                    <a href="#">소개</a> |
                    <a href="#">이용약관</a> |
                    <a href="#">개인정보처리방침</a> |
                    <a href="#">청소년보호정책</a>
                </li>
            </ul>
            <p>문의: admin@theqoo.net</p>
            <div class="copylight">
                <p>Copyright © 더꾸. All rights reserved.</p>
            </div>
        </div>
    </div>
</div>

<!-- 공통 스크립트 -->
<script src="${pageContext.request.contextPath}/resources/js/questChecker.js"></script>
<script>
    // QuestChecker 초기화
    document.addEventListener('DOMContentLoaded', function() {
        QuestChecker.init('${pageContext.request.contextPath}');
    });
</script>

