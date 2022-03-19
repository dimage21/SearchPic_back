package dimage.searchpic.service.auth;
import dimage.searchpic.domain.member.ProviderName;
import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.auth.UnsupportedOauthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OauthProvider {
    private final RestTemplate restTemplate;
    private final List<OauthInfo> oauthInfoList;

    public UserInfo getUserInfo(String accessToken, String provider) {
        OauthInfo oauthInfo = getInfo(provider);
        return oauthInfo.getUserInfo(accessToken, restTemplate);
    }

    public OauthInfo getInfo(String provider) {
        return oauthInfoList.stream().filter(oauthInfo -> oauthInfo.isProvider(ProviderName.create(provider)))
                .findFirst().orElseThrow(() -> new UnsupportedOauthException(ErrorInfo.UNSUPPORTED_OAUTH));
    }
}
