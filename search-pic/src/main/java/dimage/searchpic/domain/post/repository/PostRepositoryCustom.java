package dimage.searchpic.domain.post.repository;

import dimage.searchpic.domain.post.Post;
import dimage.searchpic.dto.tag.SearchOrder;
import java.util.List;

public interface PostRepositoryCustom {
    List<Post> getFilteredPosts(List<String> tagNames, long offset, int size, SearchOrder order);
}