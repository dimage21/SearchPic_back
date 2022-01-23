package dimage.searchpic.service;

import dimage.searchpic.domain.member.Member;
import dimage.searchpic.domain.member.repository.MemberRepository;
import dimage.searchpic.dto.member.MemberResponse;
import dimage.searchpic.dto.member.NicknameChangeRequest;
import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.common.NotFoundException;
import dimage.searchpic.exception.member.NicknameDuplicateException;
import dimage.searchpic.util.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final FileStorage fileStorage;

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ErrorInfo.MEMBER_NULL));
    }

    @Transactional
    public void updateProfile(NicknameChangeRequest changeRequest, MultipartFile profileImage, Long memberId) {
        Member member = findMemberById(memberId);

        // 변경하려는 닉네임이 이미 존재하는 지 체크
        if (!member.getNickname().equals(changeRequest.getNickname()) && memberRepository.findByNickname(changeRequest.getNickname())) {
            throw new NicknameDuplicateException(ErrorInfo.DUPLICATE_NICKNAME);
        }

        if(profileImage != null){ // 프로필 사진도 변경하는 경우
            String storedFilePath = fileStorage.storeFile(profileImage, member.getId());
            member.update(changeRequest.getNickname(),storedFilePath);
            return;
        }
        // 닉네임만 변경하는 경우
        member.update(changeRequest.getNickname());
    }

    public MemberResponse getProfileInfo(Long memberId) {
        Member member = findMemberById(memberId);
        return MemberResponse.of(member);
    }
}