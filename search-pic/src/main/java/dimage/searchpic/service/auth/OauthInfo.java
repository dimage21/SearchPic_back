package dimage.searchpic.service.auth;

import dimage.searchpic.domain.member.ProviderName;
import org.springframework.web.client.RestTemplate;

public interface OauthInfo {
    UserInfo getUserInfo(String accessToken, RestTemplate restTemplate);
    boolean isProvider(ProviderName provider);
}