<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Visual Novel Game</title>
    <link rel="stylesheet" href="resources/css/style.css">
</head>
<body>
    <div class="container">
        <div class="main-menu">
            <h1>Visual Novel Game</h1>
            <div class="menu-buttons">
                <a href="game?scene=1" class="btn">새 게임</a>
                <a href="views/game/load.jsp" class="btn">이어하기</a>
                <a href="views/user/login.jsp" class="btn">로그인</a>
                <a href="views/user/register.jsp" class="btn">회원가입</a>
            </div>
        </div>
    </div>
</body>
</html>
