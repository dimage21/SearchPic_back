package dimage.searchpic.domain.posttag.repository;

import dimage.searchpic.domain.posttag.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag,Long> {
}
