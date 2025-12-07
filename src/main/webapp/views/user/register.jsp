<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>회원가입 - 더꾸</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/board.css">
</head>
<body class="login-body">
    <div class="login-wrapper">
        <div class="login-box">
            <h1 class="login-logo"><a href="${pageContext.request.contextPath}/index.jsp">더꾸</a></h1>
            
            <%-- 에러 메시지 표시 --%>
            <c:if test="${not empty error}">
                <div style="color: #ff4757; text-align: center; margin-bottom: 15px; font-size: 13px;">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/views/user/registerAction.jsp" method="post" class="login-form">
                <div class="input-group">
                    <input type="text" name="userId" placeholder="아이디" required>
                </div>
                <div class="input-group">
                    <input type="password" name="password" placeholder="비밀번호 (6자 이상)" required minlength="6">
                </div>
                <div class="input-group">
                    <input type="password" name="passwordConfirm" placeholder="비밀번호 확인" required minlength="6">
                </div>
                <div class="input-group">
                    <input type="text" name="nickname" placeholder="닉네임" required>
                </div>
                <div class="input-group">
                    <textarea name="bio" placeholder="한 줄 소개 (선택)" rows="3" style="width: 100%; padding: 12px 15px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; font-family: inherit; resize: vertical;"></textarea>
                </div>
                
                <button type="submit" class="btn-login">회원가입</button>
                
                <div class="login-options" style="justify-content: center;">
                    <div class="login-links">
                        이미 계정이 있으신가요? <a href="${pageContext.request.contextPath}/views/user/login.jsp" style="margin-left: 5px;">로그인</a>
                    </div>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
