package dimage.searchpic.config.auth;

import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.auth.ExpiredTokenException;
import dimage.searchpic.util.TokenExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    private final PathMatcher pathMatcher = new AntPathMatcher();
    private final List<String> excludePaths = Arrays.asList("/login/**", "/locations/filter", "/posts/search/**",
                                                            "/tags", "/**/posts/**", "/location","/api/**");
    private final List<String> includePaths = Arrays.asList("/posts/member/**");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!isExcludedCondition(request.getRequestURI(), request.getMethod())) {
            validateToken(request);
        }
        return true;
    }

    // 제외하려는 path에 포함되고, 포함하려는 path에 포함되지 않고, GET으로 요청될 경우에만 토큰 검증을 제외한다.
    private boolean isExcludedCondition(String requestPath, String requestMethod) {
        boolean isGetMethod = HttpMethod.GET.matches(requestMethod);

        boolean isExcludedPath = excludePaths.stream().anyMatch(
                pathPattern -> pathMatcher.match(pathPattern, requestPath));

        boolean isIncludePath = includePaths.stream().anyMatch(
                pathPattern -> pathMatcher.match(pathPattern, requestPath));
        return isGetMethod && isExcludedPath && !isIncludePath;
    }

    // 요청의 헤더에 포함된 토큰 값의 유효성을 판단하고, 유효한 토큰이 아닐 경우 이에 해당하는 예외를 발생시킨다.
    private void validateToken(HttpServletRequest request) {
        String token = TokenExtractor.resolveToken(request);
        boolean valid = jwtTokenProvider.isValidToken(token,"access");

        if (!valid) // 액세스 토큰 만료된 경우
            throw new ExpiredTokenException(ErrorInfo.EXPIRED_TOKEN);
    }
}
