package dimage.searchpic.service.auth;

import dimage.searchpic.config.auth.JwtTokenProvider;
import dimage.searchpic.domain.member.Member;
import dimage.searchpic.domain.member.Provider;
import dimage.searchpic.domain.member.repository.MemberRepository;
import dimage.searchpic.dto.auth.LoginInfoRequest;
import dimage.searchpic.dto.auth.TokenResponse;
import dimage.searchpic.domain.token.repository.RedisTokenRepository;
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
    private final RedisTokenRepository redisTokenRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public TokenResponse loginOrSignUpAndCreateTokens(LoginInfoRequest loginRequest) {
        Member findMember = memberRepository.findByProviderId(loginRequest.getProvider().getProviderId(), loginRequest.getProvider().getProviderName()).orElse(null);
        Member member = findOrCreateMember(findMember, loginRequest);
        // pk 로 자체 액세스 토큰 생성 후 리턴
        TokenResponse tokenResponse = jwtTokenProvider.createAccessAndRefreshTokens(member.getId().toString());
        // redis 에 refreshToken 정보 저장
        redisTokenRepository.setRefreshTokenWithExpireTime(Long.toString(member.getId()),tokenResponse.getRefreshToken(),RefreshTokenInfo.refreshTokenExpireTime);
        return tokenResponse;
    }

    @Transactional
    private Member findOrCreateMember(Member findMember, LoginInfoRequest loginInfo){
        if (findMember == null) {
            Provider newProvider = Provider.builder()
                    .providerId(loginInfo.getProvider().getProviderId())
                    .providerName(loginInfo.getProvider().getProviderName())
                    .build();

            Member member = Member.builder()
                    .nickname(UUID.randomUUID().toString())
                    .email(loginInfo.getEmail())
                    .provider(newProvider)
                    .build();
            log.info("새로운 사용자 입니다.");
            return memberRepository.save(member);
        }
        log.info("이미 등록된 사용자 입니다.");
        return findMember;
    }

    // 리프레시 토큰 삭제: 만약 가장 최근에 발급한 리프레시 토큰으로 요청하지 않은 경우, 먼료된 토큰이라는 예외를 발생시키고 최근에 발급한 토큰이라면 해당 토큰을 삭제한다.
    @Transactional
    public void logout(String refreshToken) {
        jwtTokenProvider.isValidToken(refreshToken,"refresh");
        String userId = jwtTokenProvider.getPkFromToken(refreshToken,"refresh");
        redisTokenRepository.deleteRefreshToken(userId,refreshToken);
    }

    // 리프레시 토큰의 유효성 검사 -> 해당 리프레시 토큰이 유효하면서 가장 최근에 발급한 리프레시 토큰이라면 새로운 액세스 토큰과 새로운 리프레시 토큰을 발급한다.
    @Transactional
    public TokenResponse reissue(String refreshToken) {
        jwtTokenProvider.isValidToken(refreshToken,"refresh");
        String userId = jwtTokenProvider.getPkFromToken(refreshToken,"refresh");
        redisTokenRepository.validIsRecentAndUsableToken(userId, refreshToken);
        TokenResponse tokenResponse = jwtTokenProvider.createAccessAndRefreshTokens(userId);
        redisTokenRepository.setRefreshTokenWithExpireTime(userId,tokenResponse.getRefreshToken(),RefreshTokenInfo.refreshTokenExpireTime);
        return tokenResponse;
    }
}
