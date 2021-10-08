package dimage.searchpic.config.auth;

import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.auth.UnauthorizedMemberException;
import dimage.searchpic.util.TokenExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        validateToken(request); // 토큰 검증
        log.info("preHandle called");
        return true;
    }

    private void validateToken(HttpServletRequest request) {
        String token = TokenExtractor.resolveToken(request);
        boolean valid = jwtTokenProvider.validateToken(token);

        if (!valid) // 토큰 만료된 경우
            throw new UnauthorizedMemberException(ErrorInfo.NOT_AUTHORIZED_USER);
    }
}