package util;

import java.io.InputStream;
import java.util.Properties;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

/**
 * Gemini API 호출을 담당하는 서비스 클래스
 * 공식 Google Gen AI SDK 사용
 * 싱글톤 패턴 적용
 */
public class GeminiService {
    private static GeminiService instance;
    private Client client;
    private String model;
    private double temperature;
    private int maxTokens;

    private GeminiService() {
        loadConfig();
    }

    public static GeminiService getInstance() {
        if (instance == null) {
            synchronized (GeminiService.class) {
                if (instance == null) {
                    instance = new GeminiService();
                }
            }
        }
        return instance;
    }

    /**
     * gemini.properties 파일에서 설정 로드 및 Client 초기화
     */
    private void loadConfig() {
        Properties props = new Properties();
        try {
            // classpath에서 gemini.properties 로드 (src/main/resources/)
            InputStream is = getClass().getClassLoader().getResourceAsStream("gemini.properties");
            if (is == null) {
                System.err.println("GeminiService: gemini.properties 파일을 찾을 수 없습니다.");
                System.err.println("파일 위치: src/main/resources/gemini.properties");
                return;
            }

            props.load(is);
            is.close();

            String apiKey = props.getProperty("gemini.api.key");
            this.model = props.getProperty("gemini.model", "gemini-2.0-flash-exp");
            this.temperature = Double.parseDouble(props.getProperty("gemini.temperature", "0.7"));
            this.maxTokens = Integer.parseInt(props.getProperty("gemini.max_tokens", "1000"));

            // 공식 SDK Client 생성
            this.client = Client.builder()
                .apiKey(apiKey)
                .build();

            System.out.println("GeminiService: 설정 로드 완료 (모델: " + model + ")");
        } catch (Exception e) {
            System.err.println("GeminiService: 설정 파일 로드 실패 - " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gemini API를 호출하여 텍스트 생성
     * @param prompt 프롬프트 텍스트
     * @return 생성된 텍스트
     */
    public String generateText(String prompt) {
        if (client == null) {
            System.err.println("GeminiService: Client가 초기화되지 않았습니다.");
            return null;
        }

        try {
            // 공식 SDK를 사용한 텍스트 생성
            GenerateContentResponse response = client.models.generateContent(
                model,
                prompt,
                null  // GenerateContentConfig는 필요시 추가
            );

            String generatedText = response.text();

            if (generatedText != null && !generatedText.isEmpty()) {
                return generatedText.trim();
            } else {
                System.err.println("GeminiService: 응답이 비어있습니다.");
                return null;
            }

        } catch (Exception e) {
            System.err.println("GeminiService: 텍스트 생성 중 오류 - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Temperature 설정 (0.0 ~ 2.0)
     * 참고: SDK 1.28.0에서는 GenerateContentConfig로 설정
     */
    public void setTemperature(double temperature) {
        this.temperature = Math.max(0.0, Math.min(2.0, temperature));
    }

    /**
     * 최대 토큰 수 설정
     */
    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    /**
     * 모델 이름 설정
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * 현재 설정된 모델 이름 반환
     */
    public String getModel() {
        return this.model;
    }
}
