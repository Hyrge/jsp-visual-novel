<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    request.setCharacterEncoding("UTF-8");
    String id = request.getParameter("id");
    String password = request.getParameter("password");

    if ("admin".equals(id) && "1234".equals(password)) {
        session.setAttribute("userID", id);
        response.sendRedirect(request.getContextPath() + "/views/board/kdolTalkBoard.jsp");
    } else {
%>
    <script>
        alert('아이디 또는 비밀번호가 틀립니다.');
        history.back();
    </script>
<%
    }
%>
