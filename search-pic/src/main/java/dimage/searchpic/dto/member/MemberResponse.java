package dimage.searchpic.dto.member;

import dimage.searchpic.domain.member.Member;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ApiModel(value = "사용자 프로필 정보 응답")
public class MemberResponse {
    @ApiModelProperty(value = "닉네임")
    private String nickname;
    @ApiModelProperty(value = "이메일")
    private String email;
    @ApiModelProperty(value = "프로필 URL")
    private String profileUrl;
    @ApiModelProperty(value = "유저가 업로드한 글 개수")
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
