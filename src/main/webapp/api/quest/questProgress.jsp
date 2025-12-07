<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.GameContext" %>
<%@ page import="model.entity.Quest" %>
<%@ page import="model.entity.QuestObjective" %>
<%@ page import="model.enums.QuestStatus" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="com.google.gson.GsonBuilder" %>
<%@ page import="com.google.gson.JsonObject" %>
<%@ page import="com.google.gson.JsonParser" %>
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
        // POST body 읽기
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        JsonObject json = JsonParser.parseString(sb.toString()).getAsJsonObject();

        String questId = json.has("questId") ? json.get("questId").getAsString() : null;
        int objectiveId = json.has("objectiveId") ? json.get("objectiveId").getAsInt() : -1;
        int increment = json.has("increment") ? json.get("increment").getAsInt() : 0;

        if (questId == null || questId.trim().isEmpty()) {
            result.put("success", false);
            result.put("error", "퀘스트 ID가 필요합니다.");
            out.print(gson.toJson(result));
            return;
        }

        // 퀘스트 찾기
        Quest quest = gameContext.getQuestService().findQuestById(questId);

        if (quest == null) {
            result.put("success", false);
            result.put("error", "퀘스트를 찾을 수 없습니다: " + questId);
            out.print(gson.toJson(result));
            return;
        }

        boolean updated = false;

        // objectiveId가 있으면 해당 objective 완료 처리
        if (objectiveId > 0) {
            updated = gameContext.getQuestService().completeObjective(questId, objectiveId);
        }
        // increment가 있으면 진행도 증가
        else if (increment > 0) {
            gameContext.getQuestService().addQuestProgress(questId, increment);
            updated = true;
        }

        // 결과 반환
        quest = gameContext.getQuestService().findQuestById(questId); // 갱신된 퀘스트

        result.put("success", updated);
        result.put("questId", quest.getId());
        result.put("title", quest.getTitle());
        result.put("status", quest.getStatus().name());
        result.put("currentProgress", quest.getCompletedCount());
        result.put("requiredProgress", quest.getTotalCount());
        result.put("rewardReputation", quest.getRewardReputation());

        // objectives 정보
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
            result.put("objectives", objList);
        }

        // 완료된 경우 다음 퀘스트 정보
        if (quest.getStatus() == QuestStatus.COMPLETED && quest.hasNextQuest()) {
            Quest nextQuest = gameContext.getQuestService().findQuestById(quest.getNextQuestId());
            if (nextQuest != null) {
                Map<String, Object> next = new HashMap<>();
                next.put("id", nextQuest.getId());
                next.put("title", nextQuest.getTitle());
                next.put("status", nextQuest.getStatus().name());
                result.put("nextQuest", next);
            }
        }

    } catch (Exception e) {
        result.put("success", false);
        result.put("error", e.getMessage());
        e.printStackTrace();
    }

    out.print(gson.toJson(result));
%>
