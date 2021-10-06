package dimage.searchpic.service.auth;

import dimage.searchpic.dto.auth.AccessTokenRequest;
import dimage.searchpic.dto.auth.OauthUserInfoResponse.KakaoInfoResponse;
import dimage.searchpic.util.ResponseConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import static dimage.searchpic.service.auth.OauthInfo.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OauthKakaoService implements OauthService {
    private final RestTemplate restTemplate;
    private final OauthInfo.KaKao kakaoInfo;
    private final ResponseConverter responseConverter;
    @Override
    public String requestOauthToken(String code) {
        log.info("kakao [oauthRequestToken] is called");
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, DEFAULT_CHARSET);
        final AccessTokenRequest kakaoAccessTokenRequest = new AccessTokenRequest(
                kakaoInfo.getGrantType(),
                kakaoInfo.getClientId(),
                kakaoInfo.getBaseUrl()+kakaoInfo.getRedirect_uri(),
                code,
                kakaoInfo.getSecret()
        );
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    kakaoInfo.getTokenUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(
                            responseConverter.convertToParams(kakaoAccessTokenRequest), //body
                            headers // header
                    ),
                    String.class //responseType
            );

            String accessToken = responseConverter.extractFieldValueFromJsonString(response.getBody(), ACCESS_TOKEN);
            return accessToken;

        } catch (RestClientException e) {
            //TODO throw custom error
            throw new RuntimeException();
        }
    }

    @Override
    public UserInfo getUserOauthInfo(String accessToken) {
        log.info("kakao [getUserOauthInfo] is called");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.add(CONTENT_TYPE,DEFAULT_CHARSET);
        try {
            ResponseEntity<KakaoInfoResponse> response = restTemplate.exchange(
                    kakaoInfo.getInfoUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(headers),
                    KakaoInfoResponse.class
            );

            KakaoInfoResponse body = response.getBody();

            return UserInfo.builder()
                    .email(body.getKakaoAccount().getEmail())
                    .profileUrl(body.getKakaoAccount().getProfile().getImageUrl())
                    .id(body.getId()).build();
        } catch (RestClientException e) {
            //TODO throw custom error
            throw new RuntimeException();
        }
    }
}
