package dimage.searchpic.service.auth;

public interface OauthService {
    // 토큰 요청
    String requestOauthToken(String code);

    // 받은 토큰으로 유저 정보 가져오기
    UserInfo getUserOauthInfo(String accessToken);
}
