package dimage.searchpic.domain.post.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dimage.searchpic.domain.post.Post;
import dimage.searchpic.dto.tag.SearchOrder;
import lombok.RequiredArgsConstructor;
import java.util.List;
import static dimage.searchpic.domain.location.QLocation.location;
import static dimage.searchpic.domain.post.QPost.post;
import static dimage.searchpic.domain.posttag.QPostTag.postTag;
import static dimage.searchpic.domain.tag.QTag.tag;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> getFilteredPosts(List<String> tagNames, long offset, int size, SearchOrder order) {
        JPAQuery<Post> postJPAQuery = queryFactory
                .selectFrom(post)
                .join(post.location, location).fetchJoin()
                .join(post.postTags, postTag)
                .join(postTag.tag, tag)
                .where(tag.name.in(tagNames))
                .groupBy(post.id)
                .having(post.id.count().goe(tagNames.size()))
                .offset(offset)
                .limit(size);

        switch (order) {
            case VIEW:
                postJPAQuery.orderBy(post.view.desc());
                break;
            case RECENT:
                postJPAQuery.orderBy(post.createdDate.desc());
                break;
        }
        return postJPAQuery.fetch();
    }
}
