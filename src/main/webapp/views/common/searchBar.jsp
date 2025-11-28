<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- 검색 바 -->
<div class="board_search_bar">
    <form action="${param.action}" method="get">
        <input type="text" id="board_search" name="keyword" placeholder="${param.placeholder}" value="${param.keyword}">
        <button type="submit" class="btn-search">검색</button>
    </form>
</div>

