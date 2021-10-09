package dimage.searchpic.service.auth;

import dimage.searchpic.dto.auth.AccessTokenRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NaverInfo implements OauthInfo {
    @Value("${spring.security.oauth2.client.registration.naver.client_id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String secret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri-path}")
    private String redirect_uri;

    @Value("${spring.security.oauth2.client.registration.naver.authorization-grant-type}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.naver.authorization-grant-type}")
    private String grantType;

    @Value("${base.url}")
    private String baseUrl;

    @Value("${spring.security.oauth2.client.registration.naver.url.token}")
    private String tokenUrl;

    @Value("${spring.security.oauth2.client.registration.naver.url.info}")
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