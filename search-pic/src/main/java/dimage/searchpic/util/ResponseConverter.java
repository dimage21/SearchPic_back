package dimage.searchpic.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResponseConverter {
    private final ObjectMapper objectMapper;

    public <T> MultiValueMap<String, String> convert(T object) {
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            Map<String, String> paramMap = objectMapper.convertValue(object, new TypeReference<HashMap<String, String>>() {
            });
            params.setAll(paramMap);
            return params;
        } catch (Exception e){
            log.error("URL 파라미터 변환 중 오류가 발생. object = {}",object,e);
            throw new IllegalStateException("URL 파라미터 변환중 오류가 발생했습니다.");
        }
    }
}