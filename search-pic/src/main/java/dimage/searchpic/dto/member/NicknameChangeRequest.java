package dimage.searchpic.dto.member;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ApiModel(value = "사용자 닉네임 수정 요청")
public class NicknameChangeRequest {
    @ApiModelProperty(value = "변경할 닉네임")
    @Size(min = 1, message = "최소 한 글자 이상 입력해야 합니다.")
    private String nickname;
}
