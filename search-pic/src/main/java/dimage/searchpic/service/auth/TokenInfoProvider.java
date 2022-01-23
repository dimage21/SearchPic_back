package dimage.searchpic.service.auth;

import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.auth.BadTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenInfoProvider {
    private final List<TokenInfo> tokenKeyInfoList;

    public TokenInfo getAccessTokenInfo() {

        return tokenKeyInfoList.stream().filter(TokenInfo::isAccessToken)
                .findFirst().orElseThrow(()->new BadTokenException(ErrorInfo.UNSUPPORTED_TOKEN));
    }

    public TokenInfo getRefreshTokenInfo() {
        return tokenKeyInfoList.stream().filter(TokenInfo::isRefreshToken)
                .findFirst().orElseThrow(()->new BadTokenException(ErrorInfo.UNSUPPORTED_TOKEN));
    }

    public TokenInfo getSameTypeTokenInfo(String type) {
        return tokenKeyInfoList.stream().filter(tokenInfo -> tokenInfo.isSameTokenType(type))
                .findFirst().orElseThrow(()->new BadTokenException(ErrorInfo.UNSUPPORTED_TOKEN));
    }
}
