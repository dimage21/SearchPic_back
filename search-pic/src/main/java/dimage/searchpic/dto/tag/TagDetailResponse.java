package dimage.searchpic.dto.tag;
import dimage.searchpic.domain.tag.Tag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ApiModel("인기 태그 응답")
public class TagDetailResponse {
    @ApiModelProperty(value = "태그 id")
    long id;
    @ApiModelProperty(value = "태그 이름")
    String name;
    @ApiModelProperty(value = "해당 태그가 등록된 한 게시글 이미지 url")
    String url;

    public static TagDetailResponse of(Tag tag) {
        return new TagDetailResponse(
                tag.getId(),
                tag.getName(),
                tag.getPostTags().get(0).getPost().getPictureUrl()
        );
    }
}