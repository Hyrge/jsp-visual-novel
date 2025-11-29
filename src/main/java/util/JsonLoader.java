package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonLoader {
    public static List<Map<String, Object>> loadJSON(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File(filePath), List.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
