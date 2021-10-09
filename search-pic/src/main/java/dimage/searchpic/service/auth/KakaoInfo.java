package dimage.searchpic.service.auth;

import dimage.searchpic.dto.auth.AccessTokenRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KakaoInfo implements OauthInfo {
    @Value("${spring.security.oauth2.client.registration.kakao.client_id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String secret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri-path}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String grantType;

    @Value("${base.url}")
    private String baseUrl;

    @Value("${spring.security.oauth2.client.registration.kakao.url.token}")
    private String tokenUrl;

    @Value("${spring.security.oauth2.client.registration.kakao.url.info}")
    private String infoUrl;

    @Override
    public AccessTokenRequest getRequest(String code) {
        return new AccessTokenRequest(grantType, clientId, baseUrl+redirectUri, code, secret);
    }

    @Override
    public String getTokenUrl() {
        return tokenUrl;
    }

    @Override
    public String getInfoUrl() {
        return infoUrl;
    }
}