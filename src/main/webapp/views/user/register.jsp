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
    <style>
        .register-body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        .register-wrapper {
            width: 100%;
            max-width: 450px;
        }
        .register-box {
            background: #fff;
            border-radius: 16px;
            padding: 40px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
        }
        .register-logo {
            text-align: center;
            margin-bottom: 30px;
        }
        .register-logo a {
            font-size: 32px;
            font-weight: 700;
            color: #667eea;
            text-decoration: none;
        }
        .register-form .input-group {
            margin-bottom: 16px;
        }
        .register-form .input-group label {
            display: block;
            margin-bottom: 6px;
            font-size: 14px;
            font-weight: 500;
            color: #333;
        }
        .register-form .input-group input {
            width: 100%;
            padding: 12px 16px;
            border: 1px solid #ddd;
            border-radius: 8px;
            font-size: 14px;
            box-sizing: border-box;
            transition: border-color 0.2s;
        }
        .register-form .input-group input:focus {
            outline: none;
            border-color: #667eea;
        }
        .register-form .input-group .hint {
            font-size: 12px;
            color: #888;
            margin-top: 4px;
        }
        .register-form .input-group .error {
            font-size: 12px;
            color: #e74c3c;
            margin-top: 4px;
            display: none;
        }
        .btn-register {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: #fff;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            margin-top: 10px;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .btn-register:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
        }
        .register-footer {
            text-align: center;
            margin-top: 20px;
            font-size: 14px;
            color: #666;
        }
        .register-footer a {
            color: #667eea;
            text-decoration: none;
            font-weight: 500;
        }

        /* 에러 메시지 */
        <c:if test="${not empty error}">
        .error-box {
            background: #ffeaea;
            border: 1px solid #e74c3c;
            color: #c0392b;
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-size: 14px;
        }
        </c:if>
    </style>
</head>
<body class="register-body">
    <div class="register-wrapper">
        <div class="register-box">
            <h1 class="register-logo"><a href="${pageContext.request.contextPath}/index.jsp">더꾸</a></h1>

            <c:if test="${not empty error}">
                <div class="error-box">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/views/user/registerAction.jsp" method="post" class="register-form" onsubmit="return validateForm()">
                <div class="input-group">
                    <label for="userId">아이디 <span style="color:#e74c3c">*</span></label>
                    <input type="text" id="userId" name="userId" placeholder="영문, 숫자 4~20자" 
                           pattern="[a-zA-Z0-9]{4,20}" required value="${param.userId}">
                    <div class="hint">영문, 숫자 조합 4~20자</div>
                </div>

                <div class="input-group">
                    <label for="password">비밀번호 <span style="color:#e74c3c">*</span></label>
                    <input type="password" id="password" name="password" placeholder="8자 이상" 
                           minlength="8" required>
                    <div class="hint">8자 이상</div>
                </div>

                <div class="input-group">
                    <label for="passwordConfirm">비밀번호 확인 <span style="color:#e74c3c">*</span></label>
                    <input type="password" id="passwordConfirm" name="passwordConfirm" placeholder="비밀번호 재입력" required>
                    <div class="error" id="pwError">비밀번호가 일치하지 않습니다.</div>
                </div>

                <div class="input-group">
                    <label for="nickname">닉네임 <span style="color:#e74c3c">*</span></label>
                    <input type="text" id="nickname" name="nickname" placeholder="2~10자" 
                           minlength="2" maxlength="10" required value="${param.nickname}">
                    <div class="hint">2~10자</div>
                </div>

                <div class="input-group">
                    <label for="bio">자기소개</label>
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

        // 비밀번호 확인 실시간 체크
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

