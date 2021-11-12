package dimage.searchpic.dto.post;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ApiModel(value = "글 등록 요청")
public class PostRequest {
    @ApiModelProperty(value = "태그 배열")
    @Size(max = 5,message = "태그는 최대 5개까지 가능합니다.")
    private List<String> tags;

    @ApiModelProperty(value = "메모")
    private String memo;
}