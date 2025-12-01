<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>회원가입 - 더꾸</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/auth.css">
</head>
<body class="login-body">
    <div class="login-wrapper">
        <div class="login-box">
            <h1 class="login-logo"><a href="${pageContext.request.contextPath}/index.jsp">더꾸</a></h1>

            <c:if test="${not empty error}">
                <div class="error-box">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/views/user/registerAction.jsp" method="post" class="login-form" onsubmit="return validateForm()">
                <div class="input-group">
                    <label for="userId" class="form-label">아이디 <span class="required">*</span></label>
                    <input type="text" id="userId" name="userId" placeholder="영문, 숫자 4~20자" 
                           pattern="[a-zA-Z0-9]{4,20}" required value="${param.userId}">
                    <div class="hint">영문, 숫자 조합 4~20자</div>
                </div>

                <div class="input-group">
                    <label for="password" class="form-label">비밀번호 <span class="required">*</span></label>
                    <input type="password" id="password" name="password" placeholder="8자 이상" 
                           minlength="8" required>
                    <div class="hint">8자 이상</div>
                </div>

                <div class="input-group">
                    <label for="passwordConfirm" class="form-label">비밀번호 확인 <span class="required">*</span></label>
                    <input type="password" id="passwordConfirm" name="passwordConfirm" placeholder="비밀번호 재입력" required>
                    <div class="error" id="pwError">비밀번호가 일치하지 않습니다.</div>
                </div>

                <div class="input-group">
                    <label for="nickname" class="form-label">닉네임 <span class="required">*</span></label>
                    <input type="text" id="nickname" name="nickname" placeholder="2~10자" 
                           minlength="2" maxlength="10" required value="${param.nickname}">
                    <div class="hint">2~10자</div>
                </div>

                <div class="input-group">
                    <label for="bio" class="form-label">자기소개</label>
                    <input type="text" id="bio" name="bio" placeholder="선택사항" maxlength="100" value="${param.bio}">
                </div>

                <button type="submit" class="btn-register">가입하기</button>
            </form>

            <div class="register-footer">
                이미 계정이 있으신가요? <a href="${pageContext.request.contextPath}/views/user/login.jsp">로그인</a>
            </div>
        </div>
    </div>

    <script>
        function validateForm() {
            var pw = document.getElementById('password').value;
            var pwConfirm = document.getElementById('passwordConfirm').value;
            var pwError = document.getElementById('pwError');

            if (pw !== pwConfirm) {
                pwError.style.display = 'block';
                document.getElementById('passwordConfirm').focus();
                return false;
            }
            pwError.style.display = 'none';
            return true;
        }

        document.getElementById('passwordConfirm').addEventListener('input', function() {
            var pw = document.getElementById('password').value;
            var pwConfirm = this.value;
            var pwError = document.getElementById('pwError');
            
            if (pwConfirm && pw !== pwConfirm) {
                pwError.style.display = 'block';
            } else {
                pwError.style.display = 'none';
            }
        });
    </script>
</body>
</html>
