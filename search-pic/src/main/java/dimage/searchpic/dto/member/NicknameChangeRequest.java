package dimage.searchpic.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NicknameChangeRequest {
    @Size(min = 1, message = "최소 한 글자 이상 입력해야 합니다.")
    private String nickname;
}