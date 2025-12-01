<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // 카테고리 파라미터 유지를 위한 쿼리 스트링 생성
    String category = request.getParameter("category");
    String categoryQuery = (category != null && !category.isEmpty()) ? "&category=" + category : "";
%>
<!-- 페이지네이션 -->
<div class="pagination">
    <c:if test="${not empty param.currentPage}">
        <c:set var="currentPage" value="${param.currentPage}" />
        <c:set var="totalPages" value="${param.totalPages}" />

        <!-- 맨 앞으로 -->
        <a href="?page=1<%= categoryQuery %>" class="page-link">«</a>

        <!-- 이전 페이지 -->
        <c:if test="${currentPage > 1}">
            <a href="?page=${currentPage - 1}<%= categoryQuery %>" class="page-link">‹</a>
        </c:if>

        <!-- 페이지 번호 (현재 페이지 기준 ±4) -->
        <c:forEach var="i" begin="${currentPage - 4 < 1 ? 1 : currentPage - 4}"
                   end="${currentPage + 5 > totalPages ? totalPages : currentPage + 5}">
            <a href="?page=${i}<%= categoryQuery %>" class="page-link ${i == currentPage ? 'active' : ''}">${i}</a>
        </c:forEach>

        <!-- 다음 페이지 -->
        <c:if test="${currentPage < totalPages}">
            <a href="?page=${currentPage + 1}<%= categoryQuery %>" class="page-link">›</a>
        </c:if>

        <!-- 맨 뒤로 -->
        <a href="?page=${totalPages}<%= categoryQuery %>" class="page-link">»</a>
    </c:if>

    <!-- 파라미터 없을 때 기본 1페이지만 표시 -->
    <c:if test="${empty param.currentPage}">
        <a href="?page=1<%= categoryQuery %>" class="page-link active">1</a>
    </c:if>
</div>


