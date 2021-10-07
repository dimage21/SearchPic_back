package dimage.searchpic.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class TokenExtractor {
    public static final String AUTHORIZATION_HEADER = "Authorization"; // 헤더의 키
    public static final String BEARER_PREFIX = "Bearer "; // 값이 Bearer 로 시작

    // Request Header 에서 액세스 토큰 정보를 꺼내오기
    public static String resolveToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders(AUTHORIZATION_HEADER);

        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if (value.startsWith(BEARER_PREFIX)){
                return value.substring(7);
            }
        }
        return null;
    }
}