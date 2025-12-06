<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="service.PostService" %>
<%@ page import="service.UserActionHandler" %>
<%@ page import="dto.Post" %>
<%@ page import="util.RandomStringUtil" %>
<%@ page import="model.GameContext" %>
<%@ page import="model.entity.UserAction" %>
<%@ page import="model.enums.ActionType" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="org.apache.commons.fileupload2.core.DiskFileItemFactory" %>
<%@ page import="org.apache.commons.fileupload2.core.FileItem" %>
<%@ page import="org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.nio.file.*" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%@ page import="util.SavePathManager" %>
<%@ page import="dto.User"%>

<%
    request.setCharacterEncoding("UTF-8");

    // 액션 타입 확인
    String action = request.getParameter("action");

    if ("createPost".equals(action) && "POST".equalsIgnoreCase(request.getMethod())) {
        // 게시글 작성 처리
        GameContext gameContext = (GameContext) session.getAttribute("gameContext");
        String pid = gameContext != null ? gameContext.getPid() : null;
        User player = (User) session.getAttribute("player");


        if (pid == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            // 파일 저장 경로 설정 (SavePathManager 사용)
            String savePath = SavePathManager.getImagesPath(pid);
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            // multipart/form-data 처리 (Commons FileUpload 2.x API)
            DiskFileItemFactory factory = DiskFileItemFactory.builder()
                .setBufferSize(4096)
                .setPath(saveDir.toPath())
                .get();

            JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
            upload.setSizeMax(10 * 1024 * 1024); // 최대 10MB

            List<FileItem> items = upload.parseRequest(request);

            // 폼 데이터 변수
            String category = null;
            String title = null;
            String content = null;
            String savedFileName = null;

            for (FileItem item : items) {
                if (item.isFormField()) {
                    // 일반 폼 필드
                    String fieldName = item.getFieldName();
                    String fieldValue = item.getString(StandardCharsets.UTF_8);

                    if ("category".equals(fieldName)) {
                        category = fieldValue;
                    } else if ("title".equals(fieldName)) {
                        title = fieldValue;
                    } else if ("content".equals(fieldName)) {
                        content = fieldValue;
                    }
                } else {
                    // 파일 필드
                    String fileName = item.getName();
                    if (fileName != null && !fileName.trim().isEmpty() && item.getSize() > 0) {
                        // 파일명에서 경로 제거 (브라우저에 따라 전체 경로가 올 수 있음)
                        fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
                        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);

                        // 파일 확장자 검사 (이미지만 허용)
                        String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                        if ("jpg".equals(ext) || "jpeg".equals(ext) || "png".equals(ext) || "gif".equals(ext) || "webp".equals(ext)) {
                            // 고유한 파일명 생성 (타임스탬프 + 원본 파일명)
                            savedFileName = System.currentTimeMillis() + "_" + fileName;
                            Path filePath = Paths.get(savePath, savedFileName);
                            item.write(filePath);
                        }
                    }
                }
            }

            // 유효성 검사
            if (category == null || category.trim().isEmpty() ||
                title == null || title.trim().isEmpty() ||
                content == null || content.trim().isEmpty()) {
                out.println("<script>alert('모든 필드를 입력해주세요.'); history.back();</script>");
                return;
            }

            // GameContext에서 현재 게임 시간 가져오기
            LocalDateTime currentDateTime = null;
            if (gameContext != null) {
                currentDateTime = gameContext.getGameState().getCurrentDateTime();
            } else {
                currentDateTime = LocalDateTime.now(); // 게임 컨텍스트 없으면 현재 시간 사용
            }

            // Post 객체 생성
            Post post = new Post();
            String postId = RandomStringUtil.generatePostId();
            post.setPostId(postId);
            post.setPlayerPid(pid);
            post.setTitle(title.trim());
            post.setContent(content.trim());
            post.setBoardType("talk"); // 케이돌 토크 게시판
            post.setCategory(category);
            post.setCreatedAt(currentDateTime);
            post.setHasPictures(savedFileName != null); // 이미지가 있으면 true
            post.setLikeCount(0);
            post.setDislikeCount(0);
            post.setImageFile(savedFileName); // 이미지 파일명 설정
            post.setRelatedMina(false); // 일단 false로, 비동기로 업데이트
            post.setNickName(player.getNickname());

            // PostService를 통해 DB에 저장
            PostService postService = gameContext != null ? gameContext.getPostService() : new PostService(manager.DataManager.getInstance());
            boolean success = postService.createPost(post);

            if (success) {
                // UserAction 생성
                UserAction requestedAction = new UserAction(ActionType.CREATE_POST, postId, pid);
                requestedAction.setTitle(title.trim());
                requestedAction.setContent(content.trim());
                requestedAction.setTimestamp(currentDateTime);

                // UserActionHandler를 통해 시스템 반응 처리
                if (gameContext != null) {
                    UserActionHandler handler = new UserActionHandler(gameContext);
                    handler.handle(requestedAction);
                }

                // 작성한 게시글로 리다이렉트
                response.sendRedirect(request.getContextPath() + "/views/board/postView.jsp?id=" + postId);
                return;
            } else {
                out.println("<script>alert('게시글 저장에 실패했습니다.'); history.back();</script>");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<script>alert('오류가 발생했습니다: " + e.getMessage() + "'); history.back();</script>");
            return;
        }
    } else {
        // 잘못된 요청
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
    }
%>
