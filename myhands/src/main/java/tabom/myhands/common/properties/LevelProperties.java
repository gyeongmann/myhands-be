package tabom.myhands.common.properties;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Component
public class LevelProperties {

    private Map<String, Map<String, Integer>> levels;

    @PostConstruct
    public void loadLevels() {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("levels.json")) {
            if (inputStream == null) {
                throw new IllegalStateException("levels.json not found in resources");
            }
            levels = objectMapper.readValue(inputStream, new TypeReference<Map<String, Map<String, Integer>>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load levels.json", e);
        }
    }

    public Map<String, Map<String, Integer>> getLevels() {
        return levels;
    }
}

