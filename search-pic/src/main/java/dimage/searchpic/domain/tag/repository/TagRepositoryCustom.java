package dimage.searchpic.domain.tag.repository;

import dimage.searchpic.domain.tag.Tag;
import dimage.searchpic.dto.tag.TagResponse;
import java.util.List;

public interface TagRepositoryCustom {
    List<TagResponse> getLocationTopTags(long location_id, int limit);
    List<Tag> getTopTags(int limit);
}
