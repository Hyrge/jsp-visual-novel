package util;

import java.security.SecureRandom;

/**
 * 랜덤 문자열 생성 유틸리티 클래스
 * 게시글 ID, 댓글 ID, NPC ID 등에 사용
 */
public class RandomStringUtil {
    // 소문자 + 숫자 조합
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int DEFAULT_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    /**
     * 6자리 랜덤 문자열 생성 (소문자 + 숫자)
     * @return 랜덤 문자열 (예: "a3f9k2", "7m5n1p")
     */
    public static String generate() {
        return generate(DEFAULT_LENGTH);
    }

    /**
     * 지정된 길이의 랜덤 문자열 생성 (소문자 + 숫자)
     * @param length 생성할 문자열 길이
     * @return 랜덤 문자열
     */
    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 게시글 ID 생성 (6자리)
     * @return 게시글 ID
     */
    public static String generatePostId() {
        return "p" + generate(6);  // p + 6자리 = 7자리
    }

}
