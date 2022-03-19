package dimage.searchpic.service.auth;

import dimage.searchpic.domain.member.ProviderName;
import dimage.searchpic.dto.auth.NaverUserInfoResponse;
import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.auth.OauthInfoAccessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class NaverInfo implements OauthInfo {

    @Value("${spring.security.oauth2.client.registration.naver.url.info}")
    private String infoUrl;

    @Override
    public UserInfo getUserInfo(String accessToken, RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.add("CONTENT_TYPE","application/x-www-form-urlencoded;charset=utf-8");
        try {
            ResponseEntity<NaverUserInfoResponse> response = restTemplate.exchange(
                    infoUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(headers),
                    NaverUserInfoResponse.class
            );
            NaverUserInfoResponse body = response.getBody();
            return UserInfo.builder()
                    .email(body.getResponse().getEmail())
                    .profileUrl(body.getResponse().getProfileImage())
                    .id(body.getResponse().getId()).build();
        } catch (RestClientException e) {
            throw new OauthInfoAccessException(ErrorInfo.OAUTH_GET_INFO_FAIL);
        }
    }

    @Override
    public boolean isProvider(ProviderName providerName) {
        return providerName.equals(ProviderName.NAVER);
    }
}
