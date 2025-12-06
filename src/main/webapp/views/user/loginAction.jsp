<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dao.UserDAO" %>
<%@ page import="dto.User" %>
<%@ page import="model.GameContext" %>
<%@ page import="manager.GameController" %>
<%@ page import="java.util.UUID" %>
<%@ page import="dao.PlayerDAO" %>
<%@ page import="dao.UserDAO" %>
<%@ page import="dto.User" %>
<%
   
	GameContext gameContext = (GameContext) session.getAttribute("gameContext");
    PlayerDAO playerDAO = new PlayerDAO();
	String pid = null;
	
    if (gameContext == null || gameContext.getPid() == null) {
        pid = UUID.randomUUID().toString();
        Cookie pidCookie = new Cookie("pid", pid);
        pidCookie.setMaxAge(60 * 60 * 24 * 15);
        pidCookie.setPath("/");
        response.addCookie(pidCookie);
        gameContext = new GameContext(pid);
        GameController.getInstance().createPlayer(pid);
        session.setAttribute("gameContext", gameContext);
    }else {
        pid = gameContext.getPid();
        if(!playerDAO.exists(pid)) GameController.getInstance().createPlayer(pid);
    }
    playerDAO.updateLastAccess(pid);
%>
<%
    request.setCharacterEncoding("UTF-8");
    String userId = request.getParameter("id");
    String password = request.getParameter("password");

    // 1. 관리자 계정 체크 (하드코딩)
    if ("admin".equals(userId) && "1234".equals(password)) {
        session.setAttribute("player", new User(pid, userId, password, "회사가기싫당"));
        response.sendRedirect(request.getContextPath() + "/views/board/kdolTalkBoard.jsp");
        return;
    }

    // 2. DB에서 일반 회원 로그인 체크
    UserDAO userDAO = new UserDAO();
    User user = userDAO.login(userId, password);

    if (user != null) {
        session.setAttribute("user", user);
        response.sendRedirect(request.getContextPath() + "/views/board/kdolTalkBoard.jsp");
    } else {
%>
<script>
    alert('아이디 또는 비밀번호가 틀립니다.');
    history.back();
</script>
<% } %>