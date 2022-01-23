package dimage.searchpic.service.auth;

import dimage.searchpic.config.auth.JwtTokenProvider;
import dimage.searchpic.domain.member.Member;
import dimage.searchpic.domain.member.Provider;
import dimage.searchpic.domain.member.ProviderName;
import dimage.searchpic.domain.member.repository.MemberRepository;
import dimage.searchpic.dto.auth.TokenResponse;
import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.auth.ExpiredTokenException;
import dimage.searchpic.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final MemberRepository memberRepository;
    private final OauthProvider oauthProvider;

    @Transactional
    public TokenResponse createToken(String socialAccessToken, String provider) {
        UserInfo userOauthInfo = oauthProvider.getUserInfo(socialAccessToken, provider); // 소셜사에서 멤버 정보를 가지고 온다
        Member findMember = memberRepository.findByProviderId(userOauthInfo.getId(), ProviderName.create(provider)).orElse(null);
        Member member = findOrCreateMember(provider, userOauthInfo, findMember);
        // pk 로 자체 액세스 토큰 생성 후 리턴
        String accessToken = jwtTokenProvider.createAccessToken(member.getId().toString());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId().toString());
        // redis 에 refreshToken 정보 저장
        redisService.setValueWithExpireTime(Long.toString(member.getId()),refreshToken,RefreshTokenInfo.refreshTokenExpireTime);
        return TokenResponse.of(accessToken, refreshToken);
    }

    @Transactional
    private Member findOrCreateMember(String provider, UserInfo userInfo, Member findMember) {
        if (findMember == null) { // 유저가 등록되어 있지 않으면 db 에 등록
            Provider newProvider = Provider.builder()
                    .providerId(userInfo.getId())
                    .providerName(ProviderName.create(provider))
                    .build();

            Member member = Member.builder()
                    .nickname(UUID.randomUUID().toString())
                    .email(userInfo.getEmail())
                    .provider(newProvider)
                    .build();

            log.info("새로운 사용자 입니다.");
            memberRepository.save(member);
            return member;
        }
        log.info("이미 등록된 사용자 입니다.");
        return findMember;
    }


    // 리프레시 토큰 삭제: 만약 가장 최근에 발급한 리프레시 토큰으로 요청하지 않은 경우, 먼료된 토큰이라는 예외를 발생시키고 최근에 발급한 토큰이라면 해당 토큰을 삭제한다.
    public void logout(String refreshToken) {
        jwtTokenProvider.isValidToken(refreshToken,"refresh");
        String userId = jwtTokenProvider.getPkFromToken(refreshToken,"refresh");
        if (!redisService.getValue(userId).equals(refreshToken)) {
            throw new ExpiredTokenException(ErrorInfo.EXPIRED_TOKEN);
        }
        redisService.deleteValue(userId);
    }

    // 리프레시 토큰의 유효성 검사 -> 해당 리프레시 토큰이 유효하면서 가장 최근에 발급한 리프레시 토큰이라면 새로운 액세스 토큰과 새로운 리프레시 토큰을 발급한다.
    @Transactional
    public TokenResponse reissue(String refreshToken) {
        boolean isValid = jwtTokenProvider.isValidToken(refreshToken,"refresh");
        String userId = jwtTokenProvider.getPkFromToken(refreshToken,"refresh");

        if (!isValid || !redisService.getValue(userId).equals(refreshToken)) {
            throw new ExpiredTokenException(ErrorInfo.EXPIRED_TOKEN);
        }
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);
        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        redisService.setValueWithExpireTime(userId,newRefreshToken,RefreshTokenInfo.refreshTokenExpireTime);
        return TokenResponse.of(newAccessToken,newRefreshToken);
    }
}