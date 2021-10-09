package dimage.searchpic.service.auth;

import dimage.searchpic.config.auth.JwtTokenProvider;
import dimage.searchpic.domain.member.Member;
import dimage.searchpic.domain.member.Provider;
import dimage.searchpic.domain.member.ProviderName;
import dimage.searchpic.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final OauthService oauthService;

    public String getTokenFromProvider(String code, String provider) {
        return oauthService.requestOauthToken(code,provider);
    }

    @Transactional
    public String createToken(String accessToken, String provider) {
        UserInfo userOauthInfo = oauthService.getOauthInfo(accessToken,provider); // 소셜사에서 멤버 정보를 가지고 온다

        Member findMember = memberRepository.findByProviderId(userOauthInfo.getId(), ProviderName.create(provider)).orElse(null);
        Member member = findOrCreateMember(provider, userOauthInfo, findMember);
        // pk 로 자체 액세스 토큰 생성 후 리턴
        return jwtTokenProvider.createAccessToken(member.getId().toString());
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
}