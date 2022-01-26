package dimage.searchpic.service.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Objects;

@Component
public class AccessTokenInfo implements TokenInfo {
    @Value("${jwt.access-secret}")
    private String accessSecretKey;

    @Value("${jwt.access-expire-time}")
    private long accessTokenExpireTime; // 액세스 토큰 유효 시간

    @PostConstruct
    private void init() {
        accessSecretKey = Base64.getEncoder().encodeToString(accessSecretKey.getBytes());
    }

    @Override
    public String getSecretKey() {
        return accessSecretKey;
    }

    @Override
    public long getValidTime() {
        return accessTokenExpireTime;
    }

    @Override
    public boolean isSameTokenType(String type) {
        return Objects.equals(type, "access");
    }

    @Override
    public boolean isAccessToken() {
        return true;
    }

    @Override
    public boolean isRefreshToken() {
        return false;
    }
}
