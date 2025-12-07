<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="model.GameContext" %>
<%@ page import="model.entity.UserAction" %>
<%@ page import="model.enums.ActionType" %>
<%@ page import="service.UserActionHandler" %>
<%@ page import="dto.Post" %>
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

        // 세션에서 GameContext 가져오기
        GameContext gameContext = (GameContext) session.getAttribute("gameContext");

        if (gameContext == null) {
            result.put("success", false);
            result.put("message", "세션이 만료되었습니다.");
            out.print(mapper.writeValueAsString(result));
            return;
        }

        String pid = gameContext.getPid();
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

            // LIKE/DISLIKE 액션인 경우 게시글 정보 추가 (퀘스트 체크용)
            if ((type == ActionType.LIKE || type == ActionType.DISLIKE) && targetId != null) {
                Post post = gameContext.getPostService().getPost(targetId);
                if (post != null) {
                    result.put("isRelatedMina", post.isRelatedMina());
                }
            }
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
