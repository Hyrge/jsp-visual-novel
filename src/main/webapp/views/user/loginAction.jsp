<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dao.UserDAO" %>
<%@ page import="dto.User" %>
<%
    request.setCharacterEncoding("UTF-8");
    String userId = request.getParameter("id");
    String password = request.getParameter("password");

    // 1. 관리자 계정 체크 (하드코딩)
    if ("admin".equals(userId) && "1234".equals(password)) {
        session.setAttribute("userID", userId);
        response.sendRedirect(request.getContextPath() + "/views/board/kdolTalkBoard.jsp");
        return;
    }

    // 2. DB에서 일반 회원 로그인 체크
    UserDAO userDAO = new UserDAO();
    User user = userDAO.login(userId, password);

    if (user != null) {
        session.setAttribute("userID", user.getUserId());
        session.setAttribute("userPid", user.getPid());
        session.setAttribute("userNickname", user.getNickname());
        response.sendRedirect(request.getContextPath() + "/views/board/kdolTalkBoard.jsp");
    } else {
%>
<script>
    alert('아이디 또는 비밀번호가 틀립니다.');
    history.back();
</script>
<% } %>