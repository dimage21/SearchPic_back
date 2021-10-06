package dimage.searchpic.service.auth;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OauthInfo {
    static final String DEFAULT_CHARSET = "application/x-www-form-urlencoded;charset=utf-8";
    static final String CONTENT_TYPE = "Content-type";
    static final String ACCESS_TOKEN = "access_token";

    @Component
    @Getter
    static class KaKao {
        @Value("${spring.security.oauth2.client.registration.kakao.client_id}")
        private String clientId;


        @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
        private String secret;

        @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri-path}")
        private String redirect_uri;

        @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
        private String grantType;

        @Value("${base.url}")
        private String baseUrl;

        @Value("${spring.security.oauth2.client.registration.kakao.url.token}")
        private String tokenUrl;

        @Value("${spring.security.oauth2.client.registration.kakao.url.info}")
        private String infoUrl;
    }
}
