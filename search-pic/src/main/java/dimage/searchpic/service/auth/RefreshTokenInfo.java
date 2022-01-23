package dimage.searchpic.service.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Objects;

@Component
public class RefreshTokenInfo implements TokenInfo {
    @Value("${jwt.refresh-secret}")
    private String refreshSecretKey;

    public static final long refreshTokenExpireTime = 1000L * 60 * 60 * 24 * 7; // 리프레시 토큰 유효 시간: 1주일

    @PostConstruct
    private void init() {
        refreshSecretKey = Base64.getEncoder().encodeToString(refreshSecretKey.getBytes());
    }

    @Override
    public String getSecretKey() {
        return refreshSecretKey;
    }

    @Override
    public long getValidTime() {
        return refreshTokenExpireTime;
    }

    @Override
    public boolean isSameTokenType(String type) {
        return Objects.equals(type, "refresh");
    }

    @Override
    public boolean isAccessToken() {
        return false;
    }

    @Override
    public boolean isRefreshToken() {
        return true;
    }
}
