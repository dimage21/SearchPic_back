package dimage.searchpic.service;

import dimage.searchpic.domain.member.Member;
import dimage.searchpic.domain.member.repository.MemberRepository;
import dimage.searchpic.dto.member.MemberResponse;
import dimage.searchpic.dto.member.NicknameChangeRequest;
import dimage.searchpic.exception.member.NicknameDuplicateException;
import dimage.searchpic.service.storage.StorageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private StorageService storageService;

    private Member member;

    @BeforeEach
    public void setup() {
        member = Member.builder()
                .nickname("hi")
                .email("hi@naver.com")
                .id(1L).build();
    }

    @Test
    @DisplayName("같은 닉네임의 사용자가 있을 경우 예외 발생함")
    public void duplicateNickname() throws Exception {
        Member member2 = Member.builder().nickname("new").id(2L).build();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member2));
        when(memberRepository.findByNickname(anyString())).thenReturn(true); // 이미 해당 닉네임이 존재한다고 리턴할 것임.
        NicknameChangeRequest changeRequest = new NicknameChangeRequest("sample");

        Assertions.assertThrows(NicknameDuplicateException.class,
                () -> memberService.updateProfile(changeRequest, null, member2.getId())
        );
        assertThat(member2.getNickname()).isEqualTo("new");
    }

    @Test
    @DisplayName("존재하지 않은 닉네임으로 변경하는 경우에는 정상적으로 닉네임이 변경됨")
    public void updateNickname() throws Exception {
        //given
        Member member2 = Member.builder().nickname("new").id(2L).build();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member2));
        when(memberRepository.findByNickname(anyString())).thenReturn(false); // 해당 닉네임이 존재하지 않는다고 리턴할 것임

        //when
        NicknameChangeRequest changeRequest = new NicknameChangeRequest("newbie");
        memberService.updateProfile(changeRequest, null, member2.getId());

        //then
        assertThat(member2.getNickname()).isEqualTo("newbie");
    }

    @Test
    @DisplayName("본인의 프로필 정보 조회")
    public void getProfile() throws Exception {
        //given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        //when
        MemberResponse profileInfo = memberService.getProfileInfo(member.getId());
        //then
        assertThat(profileInfo.getNickname()).isEqualTo("hi");
        assertThat(profileInfo.getEmail()).isEqualTo("hi@naver.com");
    }

    @Test
    @DisplayName("프로필 사진만 변경한 경우")
    public void updateProfileImage() throws Exception {
        String fakeFilePath = "fake:/path";
        //given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        MockMultipartFile file = new MockMultipartFile("file", new byte[100]);
        when(storageService.storeFile(file, member.getId())).thenReturn(fakeFilePath);
        NicknameChangeRequest changeRequest = new NicknameChangeRequest("hi");

        //when
        memberService.updateProfile(changeRequest, file, member.getId());

        //then
        assertThat(member.getNickname()).isEqualTo("hi");
        assertThat(member.getProfileUrl()).isEqualTo(fakeFilePath);
    }
}