package dimage.searchpic.service.auth;

import dimage.searchpic.dto.auth.AccessTokenRequest;
import dimage.searchpic.dto.auth.KakaoUserInfoResponse;
import dimage.searchpic.dto.auth.NaverUserInfoResponse;
import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.auth.OauthInfoAccessException;
import dimage.searchpic.exception.auth.OauthTokenAccessException;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class OauthService {
    static final String DEFAULT_CHARSET = "application/x-www-form-urlencoded;charset=utf-8";
    static final String CONTENT_TYPE = "Content-type";
    static final String ACCESS_TOKEN = "access_token";

    private final RestTemplate restTemplate;
    private final ResponseConverter responseConverter;
    private final KakaoInfo kaKaoInfo;
    private final NaverInfo naverInfo;

    private OauthInfo oauthInfo;

    // 토큰 요청
    public String requestOauthToken(String code, String provider){
        getSocialInfo(provider); // 각 소셜사에 맞는 OauthInfo 로 설정하기

        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, DEFAULT_CHARSET);
        AccessTokenRequest tokenRequest = oauthInfo.getRequest(code);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    oauthInfo.getTokenUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(
                            responseConverter.convertToParams(tokenRequest), //body
                            headers // header
                    ),
                    String.class //responseType
            );

            return responseConverter.extractFieldValueFromJsonString(response.getBody(), ACCESS_TOKEN);

        } catch (RestClientException e) {
            throw new OauthTokenAccessException(ErrorInfo.OAUTH_GET_TOKEN_FAIL);
        }
    }

    private void getSocialInfo(String provider) {
        switch (provider) {
            case "kakao":
                oauthInfo = kaKaoInfo;
                break;
            case "naver":
                oauthInfo = naverInfo;
                break;
        }
    }

    public UserInfo getOauthInfo(String accessToken,String provider) {
        switch (provider) {
            case "kakao":
                return getKakaoOauthInfo(accessToken);
            case "naver":
                return getNaverOauthInfo(accessToken);
        }
        return null;
    }

    // 받은 토큰으로 카카오 유저 정보 가져오기
    UserInfo getKakaoOauthInfo(String accessToken){
        log.info("kakao [getKakaoOauthInfo] is called");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.add(CONTENT_TYPE,DEFAULT_CHARSET);
        try {
            ResponseEntity<KakaoUserInfoResponse> response = restTemplate.exchange(
                    oauthInfo.getInfoUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(headers),
                    KakaoUserInfoResponse.class
            );

            KakaoUserInfoResponse body = response.getBody();
            return UserInfo.builder()
                    .email(body.getKakaoAccount().getEmail())
                    .profileUrl(body.getKakaoAccount().getProfile().getImageUrl())
                    .id(body.getId()).build();
        } catch (RestClientException e) {
            throw new OauthInfoAccessException(ErrorInfo.OAUTH_GET_INFO_FAIL);
        }
    }

    // 받은 토큰으로 네이버 유저 정보 가져오기
    UserInfo getNaverOauthInfo(String accessToken){
        log.info("naver [getNaverOauthInfo] is called");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.add(CONTENT_TYPE,DEFAULT_CHARSET);
        try {
            ResponseEntity<NaverUserInfoResponse> response = restTemplate.exchange(
                    oauthInfo.getInfoUrl(),
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
}