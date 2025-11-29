<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>로그인 - 더꾸</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/board.css">
</head>
<body class="login-body">
    <div class="login-wrapper">
        <div class="login-box">
            <h1 class="login-logo"><a href="#">더꾸</a></h1>
            <form action="${pageContext.request.contextPath}/views/user/loginAction.jsp" method="post" class="login-form">
                <div class="input-group">
                    <input type="text" name="id" placeholder="아이디" required>
                </div>
                <div class="input-group">
                    <input type="password" name="password" placeholder="비밀번호" required>
                </div>
                <button type="submit" class="btn-login">로그인</button>
                <div class="login-options">
                    <label><input type="checkbox" name="remember"> 로그인 유지</label>
                    <div class="login-links">
                        <a href="#">아이디/비밀번호 찾기</a>
                        <a href="#">회원가입</a>
                    </div>
                </div>
            </form>
        </div>
    </div>
</body>
</html>