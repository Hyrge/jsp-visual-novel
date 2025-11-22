<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Visual Novel Game</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <div class="game-container">
        <!-- 배경 이미지 -->
        <div class="background"
             style="background-image: url('${pageContext.request.contextPath}/resources/image/${scene.background}');">
        </div>

        <!-- 캐릭터 이미지 -->
        <div class="character"
             style="background-image: url('${pageContext.request.contextPath}/resources/${scene.character}/${scene.image}');">
        </div>

        <!-- 대화창 -->
        <div class="dialogue-box">
            <div class="character-name">${scene.characterName}</div>
            <div class="dialogue-text">${scene.dialogue}</div>
        </div>

        <!-- 선택지 -->
        <div class="choices">
            <c:choose>
                <c:when test="${empty scene.choices}">
                    <!-- 선택지가 없으면 엔딩 -->
                    <a href="${pageContext.request.contextPath}/index.jsp" class="choice-btn">
                        메인으로 돌아가기
                    </a>
                </c:when>
                <c:otherwise>
                    <!-- 선택지 출력 -->
                    <c:forEach var="choice" items="${scene.choices}">
                        <%-- 호감도 변화와 루트를 파라미터로 전달 --%>
                        <c:set var="choiceParams" value="" />

                        <c:if test="${choice.minaAffection != 0}">
                            <c:set var="choiceParams" value="${choiceParams}mina:${choice.minaAffection}," />
                        </c:if>

                        <c:if test="${choice.kangwooAffection != 0}">
                            <c:set var="choiceParams" value="${choiceParams}kangwoo:${choice.kangwooAffection}," />
                        </c:if>

                        <c:if test="${not empty choice.route}">
                            <c:set var="choiceParams" value="${choiceParams}route:${choice.route}," />
                        </c:if>

                        <%-- 다음 씬으로 이동 링크 --%>
                        <c:choose>
                            <c:when test="${not empty choice.nextScene}">
                                <a href="${pageContext.request.contextPath}/game?scene=${choice.nextScene}&choice=${choiceParams}"
                                   class="choice-btn">
                                    ${choice.text}
                                </a>
                            </c:when>
                            <c:otherwise>
                                <%-- nextScene이 없으면 엔딩 --%>
                                <a href="${pageContext.request.contextPath}/index.jsp?choice=${choiceParams}"
                                   class="choice-btn">
                                    ${choice.text}
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- UI 버튼들 -->
        <div class="game-ui">
            <button onclick="alert('저장 기능은 아직 구현 중입니다.')" class="ui-btn">저장</button>
            <button onclick="toggleStatus()" class="ui-btn">상태</button>
            <a href="${pageContext.request.contextPath}/index.jsp" class="ui-btn" style="text-decoration:none;">메뉴</a>
        </div>

        <!-- 호감도 표시 패널 -->
        <div id="status-panel" class="status-panel" style="display:none;">
            <h3>현재 상태</h3>
            <p>미나 호감도: <span>${affectionMina}</span></p>
            <p>강우 호감도: <span>${affectionKangwoo}</span></p>
            <button onclick="toggleStatus()" class="ui-btn">닫기</button>
        </div>
    </div>

    <!-- 최소한의 JavaScript (상태창 토글만) -->
    <script>
        function toggleStatus() {
            var panel = document.getElementById('status-panel');
            if (panel.style.display === 'none') {
                panel.style.display = 'block';
            } else {
                panel.style.display = 'none';
            }
        }
    </script>
</body>
</html>
