<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="GameController" %>
<%
    // 게임 시스템 초기화 (서버 시작 후 최초 접속 시 1회 실행)
    GameController.getInstance().init(application.getRealPath("/"));
%>
<jsp:forward page="/views/user/login.jsp" />