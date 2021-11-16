package dimage.searchpic.domain.posttag.repository;

import dimage.searchpic.dto.tag.TagDto;

import java.util.List;

public interface PostTagRepositoryCustom {
    List<TagDto> getTopTags(long location_id, int limit);
}
