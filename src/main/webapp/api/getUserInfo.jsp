<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.NPCUser" %>
<%@ page import="manager.NPCUserManager" %>
<%@ page import="dao.UserDAO" %>
<%@ page import="dto.User" %>
<%@ page import="model.GameContext" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    // 회원정보 API - NPC 또는 플레이어 정보 반환
    request.setCharacterEncoding("UTF-8");
    
    String playerId = request.getParameter("id");
    
    if (playerId == null || playerId.trim().isEmpty()) {
        out.print("{\"error\": \"id 파라미터가 필요합니다.\"}");
        return;
    }
    
    // 먼저 NPC인지 확인
    NPCUser npc = NPCUserManager.getInstance().getNPCById(playerId);
    
    if (npc != null) {
        // NPC인 경우
        String nickname = request.getParameter("nickname");
        if (nickname == null) nickname = "유저";
        
        // JSON 응답 생성 (이스케이프 처리)
        String description = npc.getDescription() != null ? npc.getDescription().replace("\"", "\\\"") : "";
        String signupDate = npc.getSignupDate() != null ? npc.getSignupDate().replace("-", ".") : "알 수 없음";
        
        out.print("{");
        out.print("\"type\": \"npc\",");
        out.print("\"id\": \"" + playerId + "\",");
        out.print("\"nickname\": \"" + nickname.replace("\"", "\\\"") + "\",");
        out.print("\"signupDate\": \"" + signupDate + "\",");
        out.print("\"description\": \"" + description + "\"");
        out.print("}");
    } else {
        // 플레이어인 경우 - User 테이블에서 조회
        UserDAO userDAO = new UserDAO();
        User user = userDAO.findByPid(playerId);
        
        if (user != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            String signupDate = user.getCreatedAt() != null ? user.getCreatedAt().format(formatter) : "알 수 없음";
            String nickname = user.getNickname() != null ? user.getNickname().replace("\"", "\\\"") : "유저";
            String bio = user.getBio() != null ? user.getBio().replace("\"", "\\\"").replace("\n", "\\n") : "";
            String userId = user.getUserId() != null ? user.getUserId().replace("\"", "\\\"") : "";
            
            out.print("{");
            out.print("\"type\": \"player\",");
            out.print("\"nickname\": \"" + nickname + "\",");
            out.print("\"userId\": \"" + userId + "\",");
            out.print("\"signupDate\": \"" + signupDate + "\",");
            out.print("\"description\": \"" + bio + "\"");
            out.print("}");
        } else {
            out.print("{\"error\": \"사용자를 찾을 수 없습니다.\"}");
        }
    }
%>
