package dimage.searchpic.dto.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ApiModel("태그 응답")
public class TagResponse {
    @ApiModelProperty(value = "태그 id")
    long id;
    @ApiModelProperty(value = "태그 이름")
    String name;
}
