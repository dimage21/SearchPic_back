package dimage.searchpic.dto.auth;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TokenResponse {
    private final String accessToken;

    public static TokenResponse of(String accessToken) {
        return new TokenResponse(accessToken);
    }
}