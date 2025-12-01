<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dao.UserDAO" %>
<%@ page import="model.GameContext" %>
<%
    // 인코딩 설정 (UseBean 전에 해야 함)
    request.setCharacterEncoding("UTF-8");
%>

<%-- UseBean 패턴: 폼 데이터 자동 바인딩 --%>
<jsp:useBean id="user" class="dto.User" scope="page"/>
<jsp:setProperty name="user" property="userId"/>
<jsp:setProperty name="user" property="password"/>
<jsp:setProperty name="user" property="nickname"/>
<jsp:setProperty name="user" property="bio"/>

<%
    // 세션에서 GameContext의 pid 가져오기
    GameContext gameContext = (GameContext) session.getAttribute("gameContext");
    
    if (gameContext == null || gameContext.getPid() == null) {
        request.setAttribute("error", "게임을 먼저 시작해주세요.");
        request.getRequestDispatcher("register.jsp").forward(request, response);
        return;
    }
    
    // Player의 pid를 User에 설정
    user.setPid(gameContext.getPid());
    
    // 비밀번호 확인
    String passwordConfirm = request.getParameter("passwordConfirm");
    
    // 유효성 검사
    if (user.getUserId() == null || user.getUserId().trim().isEmpty()) {
        request.setAttribute("error", "아이디를 입력해주세요.");
        request.getRequestDispatcher("register.jsp").forward(request, response);
        return;
    }
    
    if (user.getPassword() == null || user.getPassword().length() < 6) {
        request.setAttribute("error", "비밀번호는 6자 이상이어야 합니다.");
        request.getRequestDispatcher("register.jsp").forward(request, response);
        return;
    }
    
    if (!user.getPassword().equals(passwordConfirm)) {
        request.setAttribute("error", "비밀번호가 일치하지 않습니다.");
        request.getRequestDispatcher("register.jsp").forward(request, response);
        return;
    }
    
    if (user.getNickname() == null || user.getNickname().trim().isEmpty()) {
        request.setAttribute("error", "닉네임을 입력해주세요.");
        request.getRequestDispatcher("register.jsp").forward(request, response);
        return;
    }
    
    // 중복 체크
    UserDAO userDAO = new UserDAO();
    
    if (userDAO.existsByUserId(user.getUserId())) {
        request.setAttribute("error", "이미 사용 중인 아이디입니다.");
        request.getRequestDispatcher("register.jsp").forward(request, response);
        return;
    }
    
    
    // 회원가입 처리
    boolean success = userDAO.insert(user);
    
    if (success) {
        // 성공: 로그인 페이지로 이동
%>
<script>
    alert('회원가입이 완료되었습니다. 로그인해주세요.');
    location.href = '<%=request.getContextPath()%>/views/user/login.jsp';
</script>
<%
    } else {
        request.setAttribute("error", "회원가입에 실패했습니다. 다시 시도해주세요.");
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }
%>

