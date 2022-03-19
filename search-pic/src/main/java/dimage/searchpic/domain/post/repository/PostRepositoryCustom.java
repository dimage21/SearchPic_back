package dimage.searchpic.domain.post.repository;

import dimage.searchpic.dto.location.MarkLocationResponse;
import dimage.searchpic.dto.post.PostResponse;
import dimage.searchpic.dto.tag.SearchOrder;
import java.util.List;

public interface PostRepositoryCustom {
    List<PostResponse> getFilteredPosts(List<String> tagNames, long offset, int size, SearchOrder order);
    List<PostResponse> getPostsMemberWrite(Long memberId, long offset, int size);
    List<MarkLocationResponse> findLocationsMemberWrite(Long memberId);
}
