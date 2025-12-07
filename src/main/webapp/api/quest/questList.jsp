<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.GameContext" %>
<%@ page import="model.entity.Quest" %>
<%@ page import="model.entity.QuestObjective" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="com.google.gson.GsonBuilder" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ArrayList" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setContentType("application/json; charset=UTF-8");

    Gson gson = new GsonBuilder().create();
    Map<String, Object> result = new HashMap<>();

    // 세션에서 GameContext 가져오기
    GameContext gameContext = (GameContext) session.getAttribute("gameContext");

    if (gameContext == null) {
        result.put("success", false);
        result.put("error", "세션이 만료되었습니다.");
        out.print(gson.toJson(result));
        return;
    }

    try {
        // 활성 퀘스트 목록 가져오기
        List<Quest> activeQuests = gameContext.getQuestService().getActiveQuests();

        // JSON 변환을 위한 간소화된 퀘스트 목록 생성
        List<Map<String, Object>> questList = new ArrayList<>();

        for (Quest quest : activeQuests) {
            Map<String, Object> q = new HashMap<>();
            q.put("id", quest.getId());
            q.put("title", quest.getTitle());
            q.put("status", quest.getStatus().name());
            q.put("currentProgress", quest.getCurrentProgress());
            q.put("requiredProgress", quest.getRequiredProgress());

            // objectives 변환
            if (quest.getObjectives() != null) {
                List<Map<String, Object>> objList = new ArrayList<>();
                for (QuestObjective obj : quest.getObjectives()) {
                    Map<String, Object> o = new HashMap<>();
                    o.put("id", obj.getId());
                    o.put("description", obj.getDescription());
                    o.put("visible", obj.isVisible());
                    o.put("completed", obj.isCompleted());
                    objList.add(o);
                }
                q.put("objectives", objList);
            }

            questList.add(q);
        }

        result.put("success", true);
        result.put("quests", questList);

    } catch (Exception e) {
        result.put("success", false);
        result.put("error", e.getMessage());
    }

    out.print(gson.toJson(result));
%>
