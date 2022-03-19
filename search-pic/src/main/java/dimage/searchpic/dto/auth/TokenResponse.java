package dimage.searchpic.dto.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ApiModel("액세스 및 리프레시 토큰 응답")
public class TokenResponse {
    @ApiModelProperty("액세스 토큰")
    private final String accessToken;

    @ApiModelProperty("리프레시 토큰")
    private final String refreshToken;

    public static TokenResponse of(String accessToken, String refreshToken) {
        return new TokenResponse(accessToken,refreshToken);
    }
}
