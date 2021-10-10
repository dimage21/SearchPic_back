package dimage.searchpic.dto.member;

import dimage.searchpic.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemberResponse {

    private String nickname;
    private String email;
    private String profileUrl;
    private Long postCount;

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .nickname(member.getNickname())
                .email(member.getEmail())
                .profileUrl(member.getProfileUrl())
                .postCount(member.getPostCount())
                .build();
    }
}
