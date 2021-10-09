package dimage.searchpic.service.auth;

import dimage.searchpic.dto.auth.AccessTokenRequest;

public interface OauthInfo {
    AccessTokenRequest getRequest(String code);
    String getTokenUrl();
    String getInfoUrl();
}