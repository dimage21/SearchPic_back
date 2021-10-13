package dimage.searchpic.service;

import dimage.searchpic.config.auth.JwtTokenProvider;
import dimage.searchpic.domain.member.Member;
import dimage.searchpic.domain.member.Provider;
import dimage.searchpic.domain.member.ProviderName;
import dimage.searchpic.domain.member.repository.MemberRepository;
import dimage.searchpic.dto.member.MemberResponse;
import dimage.searchpic.dto.member.NicknameChangeRequest;
import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.member.MemberNotFoundException;
import dimage.searchpic.exception.member.NicknameDuplicateException;
import dimage.searchpic.service.auth.OauthService;
import dimage.searchpic.service.auth.UserInfo;
import dimage.searchpic.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final OauthService oauthService;
    private final StorageService storageService;

    @Transactional
    public String createToken(String accessToken, String provider) {
        UserInfo userOauthInfo = oauthService.getOauthInfo(accessToken, provider); // 소셜사에서 멤버 정보를 가지고 온다
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

    public Member checkMemberExist(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(ErrorInfo.MEMBER_NULL));
    }

    @Transactional
    public void updateProfile(NicknameChangeRequest changeRequest, MultipartFile profileImage, Long memberId) {
        Member member = checkMemberExist(memberId);

        // 변경하려는 닉네임이 이미 존재하는 지 체크
        if (!member.getNickname().equals(changeRequest.getNickname()) && memberRepository.findByNickname(changeRequest.getNickname())) {
            throw new NicknameDuplicateException(ErrorInfo.DUPLICATE_NICKNAME);
        }

        if(profileImage != null){ // 프로필 사진도 변경하는 경우
            String storedFilePath = storageService.storeFile(profileImage, member.getId());
            member.update(changeRequest.getNickname(),storedFilePath);
            return;
        }
        // 닉네임만 변경하는 경우
        member.update(changeRequest.getNickname());
    }

    public MemberResponse getProfileInfo(Long memberId) {
        Member member = checkMemberExist(memberId);
        return MemberResponse.of(member);
    }
}