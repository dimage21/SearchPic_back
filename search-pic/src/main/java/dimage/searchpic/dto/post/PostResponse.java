package dimage.searchpic.dto.post;

import dimage.searchpic.domain.post.Post;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ApiModel("하나의 게시글 응답")
public class PostResponse {
    @ApiModelProperty("게시글 id")
    long postId;

    @ApiModelProperty("사진 url")
    String pictureUrl;

    @ApiModelProperty("태그 이름 리스트")
    List<String> tagNames;

    @ApiModelProperty("장소 주소")
    String address;

    @ApiModelProperty("글 메모")
    String description;

    @ApiModelProperty("장소 id")
    long locationId;

    public static PostResponse of(Post post) {
        return new PostResponse(
                post.getId(),
                post.getPictureUrl(),
                post.getPostTags().stream().map(postTag -> postTag.getTag().getName()).collect(Collectors.toList()),
                post.getLocation().getAddress(),
                post.getDescription(),
                post.getLocation().getId()
        );
    }
}
