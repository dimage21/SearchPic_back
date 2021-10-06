package dimage.searchpic.service.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserInfo {
    private String id; // 서비스 제공사(NAVER, KAKAO) 에게 전달받은 유저 회원번호
    private String email;
    private String profileUrl;
}