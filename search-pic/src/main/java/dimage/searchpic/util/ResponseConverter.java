package dimage.searchpic.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ResponseConverter {
    private final ObjectMapper objectMapper;

    public <T> MultiValueMap<String, String> convertToParams(T body) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Map<String, String> paramMap = objectMapper.convertValue(body, new TypeReference<HashMap<String, String>>() {});
        params.setAll(paramMap);
        return params;
    }

    public String extractFieldValueFromJsonString(String json, String name) {
        try {
            return objectMapper.readTree(json).get(name).asText();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException();
        }
    }
}
