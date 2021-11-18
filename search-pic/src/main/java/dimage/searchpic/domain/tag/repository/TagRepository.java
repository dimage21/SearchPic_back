package dimage.searchpic.domain.tag.repository;

import dimage.searchpic.domain.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag,Long>, TagRepositoryCustom {
    Optional<Tag> findByName(String name);
}
