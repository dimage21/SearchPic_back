package dimage.searchpic.domain.post.repository;

import dimage.searchpic.domain.location.Location;
import dimage.searchpic.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long>, PostRepositoryCustom {
    @Query("select p from Post p join fetch p.author a join fetch p.location l where p.id=:postId and a.id=:memberId")
    Optional<Post> getPostByAuthorAndId(@Param("postId") long postId, @Param("memberId") long memberId);

    @Query("select p from Post p join fetch p.author join fetch p.location where p.id=:postId")
    Optional<Post> findOnePostById(@Param("postId") Long postId);

    List<Post> findByLocationOrderByCreatedDateDesc(Location location);
}
