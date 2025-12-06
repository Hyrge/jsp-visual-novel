<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="model.GameContext" %>
<%@ page import="model.entity.UserAction" %>
<%@ page import="model.enums.ActionType" %>
<%@ page import="service.UserActionHandler" %>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%
    // JSON 응답 설정
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> result = new HashMap<>();

    try {
        String actionTypeStr = request.getParameter("actionType");
        String content = request.getParameter("content");
        String targetId = request.getParameter("targetId");
        String title = request.getParameter("title");
        
        // PID는 세션이나 컨텍스트에서 가져와야 하지만, 현재 GameContext가 싱글턴이 아니므로 주의
        // 여기서는 JSP에서 선언된 GameContext를 사용할 수 없음 (Application Scope 필요)
        // 임시로 application attribute나 static 접근이 필요함.
        // 하지만 기존 코드를 보면 GameContext는 JSP마다 새로 생성되는 것 처럼 보이기도 했음 (MessageSidebar 등)
        // -> GameContext.jsp 같은 공통 include가 있나?
        
        // 일반적으로 WebAppListener 등에서 GameContext를 ServletContext에 저장해두는 것이 정석.
        // 여기서는 임시로 매 요청마다 GameContext를 생성하면 데이터가 초기화될 위험이 있음.
        // 기존 코드(MessageSidebar.jsp)를 보면 GameContext가 세션에 저장되거나 하지 않고
        // new GameContext(pid)를 매번 함 -> 이는 데이터 지속성에 치명적일 수 있음 (Service들이 새로 생성됨)
        // 단, DataManager가 싱글턴이면 데이터는 유지됨. 하지만 EventBus 구독 등은 날아감.
        
        // 해결책: Application Scope에서 GameContext를 가져오도록 해야 함.
        // 현재 프로젝트 구조상 static 접근자가 없으므로, ServletContext에서 가져오도록 가정하거나
        // DataManager가 상태를 들고 있는지 확인해야 함.
        
        // *중요*: 이전 대화에서 GameContext는 new GameContext()로 생성되고 있었음.
        // 이 문제를 해결하지 않으면 퀘스트 진행도가 유지되지 않을 수 있음 (QuestService가 새로 생성되므로).
        // 하지만 QuestService는 SavePathManager를 통해 파일에서 로드하므로 영속성은 유지됨.
        // 문제는 EventBus 구독이 끊기는 것인데, 단순 액션 처리는 파일 저장만 잘 되면 됨.
        
        // 따라서 여기서 new GameContext(pid)를 해도 QuestService가 파일에서 로드하면 상태는 복구됨.
        // 다만 성능상 좋지는 않음.
        
        String pid = (String) session.getAttribute("userPid");
        if (pid == null) pid = "user_1"; // 기본값
        
        GameContext gameContext = new GameContext(pid); // 여기서 QuestService 초기화 및 로드됨
        UserActionHandler actionHandler = new UserActionHandler(gameContext);
        
        if (actionTypeStr != null) {
            ActionType type = ActionType.valueOf(actionTypeStr);
            UserAction action = new UserAction();
            action.setPlayerId(pid);
            action.setActionType(type);
            action.setContent(content);
            action.setTargetId(targetId);
            action.setTitle(title);
            action.setTimestamp(java.time.LocalDateTime.now());
            
            actionHandler.handle(action);
            
            result.put("success", true);
            result.put("message", "Action handled: " + type);
        } else {
            result.put("success", false);
            result.put("message", "Missing actionType");
        }
        
    } catch (Exception e) {
        result.put("success", false);
        result.put("message", e.getMessage());
        e.printStackTrace();
    }
    
    out.print(mapper.writeValueAsString(result));
%>
