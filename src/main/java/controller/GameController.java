package controller;

import dto.Scene;
import dto.Choice;
import service.SceneManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * 게임 진행을 처리하는 서블릿
 */
@WebServlet("/game")
public class GameController extends HttpServlet {
    private SceneManager sceneManager;

    @Override
    public void init() throws ServletException {
        // 서블릿 초기화 시 SceneManager 생성
        sceneManager = new SceneManager("story.json");
        System.out.println("GameController 초기화 완료. 씬 개수: " + sceneManager.getSceneCount());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 씬 번호 가져오기 (기본값 1)
        String sceneParam = request.getParameter("scene");
        int sceneId = 1;

        if (sceneParam != null) {
            try {
                sceneId = Integer.parseInt(sceneParam);
            } catch (NumberFormatException e) {
                sceneId = 1;
            }
        }

        // 선택지 처리 (choice 파라미터가 있으면)
        String choiceParam = request.getParameter("choice");
        if (choiceParam != null) {
            processChoice(request, choiceParam);
        }

        // 해당 씬 가져오기
        Scene scene = sceneManager.getScene(sceneId);

        if (scene == null) {
            // 씬이 없으면 에러 페이지로
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "씬을 찾을 수 없습니다.");
            return;
        }

        // 세션에서 호감도 가져오기
        HttpSession session = request.getSession();
        Integer affectionMina = (Integer) session.getAttribute("affectionMina");
        Integer affectionKangwoo = (Integer) session.getAttribute("affectionKangwoo");

        // 세션에 없으면 초기화
        if (affectionMina == null) {
            affectionMina = 0;
            session.setAttribute("affectionMina", affectionMina);
        }
        if (affectionKangwoo == null) {
            affectionKangwoo = 0;
            session.setAttribute("affectionKangwoo", affectionKangwoo);
        }

        // request에 씬 데이터 저장
        request.setAttribute("scene", scene);
        request.setAttribute("affectionMina", affectionMina);
        request.setAttribute("affectionKangwoo", affectionKangwoo);

        // JSP로 포워딩
        request.getRequestDispatcher("/views/game/game.jsp").forward(request, response);
    }

    /**
     * 선택지 처리 - 호감도 업데이트
     */
    private void processChoice(HttpServletRequest request, String choiceParam) {
        try {
            // choice 파라미터 파싱 (예: "mina:5,kangwoo:10,route:mina")
            String[] parts = choiceParam.split(",");
            HttpSession session = request.getSession();

            for (String part : parts) {
                String[] keyValue = part.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();

                    if ("mina".equals(key)) {
                        // 미나 호감도 업데이트
                        int currentAffection = (Integer) session.getAttribute("affectionMina");
                        int change = Integer.parseInt(value);
                        session.setAttribute("affectionMina", currentAffection + change);
                    } else if ("kangwoo".equals(key)) {
                        // 강우 호감도 업데이트
                        int currentAffection = (Integer) session.getAttribute("affectionKangwoo");
                        int change = Integer.parseInt(value);
                        session.setAttribute("affectionKangwoo", currentAffection + change);
                    } else if ("route".equals(key)) {
                        // 루트 업데이트
                        session.setAttribute("gameRoute", value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
