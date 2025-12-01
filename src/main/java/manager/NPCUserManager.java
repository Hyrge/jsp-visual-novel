package manager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import model.NPCUser;

/**
 * NPC 사용자 프로필을 관리하는 매니저 클래스
 * profile_config.json 파일을 로드하고 NPC 목록을 관리
 */
public class NPCUserManager {
    private static NPCUserManager instance;
    private List<NPCUser> npcUsers;
    private static final String CONFIG_PATH = "/profile_config.json";

    // 싱글톤 패턴
    private NPCUserManager() {
        loadNPCProfiles();
    }

    public static NPCUserManager getInstance() {
        if (instance == null) {
            synchronized (NPCUserManager.class) {
                if (instance == null) {
                    instance = new NPCUserManager();
                }
            }
        }
        return instance;
    }

    /**
     * profile_config.json 파일에서 NPC 프로필 로드
     */
    private void loadNPCProfiles() {
        try {
            InputStream is = getClass().getResourceAsStream(CONFIG_PATH);
            if (is == null) {
                throw new RuntimeException("profile_config.json 파일을 찾을 수 없습니다.");
            }

            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<NPCUser>>(){}.getType();
            npcUsers = gson.fromJson(reader, listType);

            reader.close();

            System.out.println("[NPCUserManager] " + npcUsers.size() + "명의 NPC 프로필 로드 완료");
        } catch (Exception e) {
            e.printStackTrace();
            npcUsers = new ArrayList<>();
        }
    }

    /**
     * 모든 NPC 사용자 목록 반환
     * @return NPC 사용자 리스트
     */
    public List<NPCUser> getAllNPCUsers() {
        return new ArrayList<>(npcUsers);
    }

    /**
     * ID로 NPC 사용자 찾기
     * @param id NPC ID
     * @return NPCUser 객체, 없으면 null
     */
    public NPCUser getNPCById(String id) {
        return npcUsers.stream()
                .filter(npc -> npc.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * NPC 타입별로 필터링
     * @param npcType NPC 타입 (학생, 직장인, 주부, 무직, 대학생, 프리랜서)
     * @return 해당 타입의 NPC 리스트
     */
    public List<NPCUser> getNPCsByType(String npcType) {
        return npcUsers.stream()
                .filter(npc -> npc.getNpcType().equals(npcType))
                .collect(Collectors.toList());
    }

    /**
     * 안티 NPC만 필터링
     * @return 안티 NPC 리스트 (baseSentiment < 0)
     */
    public List<NPCUser> getAntiNPCs() {
        return npcUsers.stream()
                .filter(NPCUser::isAnti)
                .collect(Collectors.toList());
    }

    /**
     * 팬 NPC만 필터링
     * @return 팬 NPC 리스트 (baseSentiment > 3)
     */
    public List<NPCUser> getFanNPCs() {
        return npcUsers.stream()
                .filter(NPCUser::isFan)
                .collect(Collectors.toList());
    }

    /**
     * 현재 시간에 온라인인 NPC 필터링
     * @param currentTime 현재 게임 시간 (HH:mm 형식)
     * @return 온라인 상태인 NPC 리스트
     */
    public List<NPCUser> getOnlineNPCs(String currentTime) {
        return npcUsers.stream()
                .filter(npc -> npc.isOnline(currentTime))
                .collect(Collectors.toList());
    }

    /**
     * 랜덤으로 NPC 선택
     * @return 랜덤 NPCUser
     */
    public NPCUser getRandomNPC() {
        if (npcUsers.isEmpty()) {
            return null;
        }
        int index = (int) (Math.random() * npcUsers.size());
        return npcUsers.get(index);
    }

    /**
     * 특정 조건의 NPC 중에서 랜덤 선택
     * @param npcList NPC 리스트
     * @return 랜덤 NPCUser, 리스트가 비어있으면 null
     */
    public NPCUser getRandomNPCFromList(List<NPCUser> npcList) {
        if (npcList == null || npcList.isEmpty()) {
            return null;
        }
        int index = (int) (Math.random() * npcList.size());
        return npcList.get(index);
    }

    /**
     * 여론에 따라 반응할 NPC 선택
     * @param currentSentiment 현재 여론 (0~100)
     * @param currentTime 현재 시간
     * @return 선택된 NPC 리스트 (3~7명)
     */
    public List<NPCUser> selectNPCsForReaction(int currentSentiment, String currentTime) {
        List<NPCUser> onlineNPCs = getOnlineNPCs(currentTime);
        List<NPCUser> selectedNPCs = new ArrayList<>();

        // 여론이 높으면 팬 NPC가 더 많이 반응
        // 여론이 낮으면 안티 NPC가 더 많이 반응
        int fanCount = currentSentiment / 20;  // 0~5명
        int antiCount = (100 - currentSentiment) / 20;  // 0~5명

        // 팬 NPC 선택
        List<NPCUser> onlineFans = onlineNPCs.stream()
                .filter(NPCUser::isFan)
                .collect(Collectors.toList());
        for (int i = 0; i < fanCount && !onlineFans.isEmpty(); i++) {
            NPCUser selected = getRandomNPCFromList(onlineFans);
            if (selected != null && !selectedNPCs.contains(selected)) {
                selectedNPCs.add(selected);
                onlineFans.remove(selected);
            }
        }

        // 안티 NPC 선택
        List<NPCUser> onlineAntis = onlineNPCs.stream()
                .filter(NPCUser::isAnti)
                .collect(Collectors.toList());
        for (int i = 0; i < antiCount && !onlineAntis.isEmpty(); i++) {
            NPCUser selected = getRandomNPCFromList(onlineAntis);
            if (selected != null && !selectedNPCs.contains(selected)) {
                selectedNPCs.add(selected);
                onlineAntis.remove(selected);
            }
        }

        // 중립 NPC도 일부 추가
        List<NPCUser> neutralNPCs = onlineNPCs.stream()
                .filter(npc -> !npc.isAnti() && !npc.isFan())
                .collect(Collectors.toList());
        int neutralCount = 2 + (int) (Math.random() * 2);  // 2~3명
        for (int i = 0; i < neutralCount && !neutralNPCs.isEmpty(); i++) {
            NPCUser selected = getRandomNPCFromList(neutralNPCs);
            if (selected != null && !selectedNPCs.contains(selected)) {
                selectedNPCs.add(selected);
                neutralNPCs.remove(selected);
            }
        }

        return selectedNPCs;
    }

    /**
     * NPC 개수 반환
     * @return 전체 NPC 수
     */
    public int getNPCCount() {
        return npcUsers.size();
    }

    /**
     * 설정 파일 리로드 (테스트용)
     */
    public void reload() {
        loadNPCProfiles();
    }
}
