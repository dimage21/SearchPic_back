package dimage.searchpic.dto.posttag;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostTagDto {
    String tagName;
    Long tagId;
    Long postId;

    public PostTagDto(String tagName, Long tagId, Long postId) {
        this.tagName = tagName;
        this.tagId = tagId;
        this.postId = postId;
    }
}