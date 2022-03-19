package dimage.searchpic.domain.tag.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dimage.searchpic.domain.tag.Tag;
import dimage.searchpic.dto.tag.TagResponse;
import lombok.RequiredArgsConstructor;
import java.util.List;
import static dimage.searchpic.domain.post.QPost.post;
import static dimage.searchpic.domain.posttag.QPostTag.postTag;
import static dimage.searchpic.domain.tag.QTag.tag;

@RequiredArgsConstructor
public
class TagRepositoryImpl implements TagRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<TagResponse> getLocationTopTags(long location_id, int limit) {
        return queryFactory
                .select(Projections.constructor(TagResponse.class, tag.id, tag.name))
                .from(tag)
                .join(tag.postTags, postTag)
                .join(postTag.post, post)
                .where(post.location.id.eq(location_id))
                .groupBy(tag.id)
                .orderBy(tag.id.count().desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Tag> getTopTags(int limit) {
        return queryFactory
                .select(tag)
                .from(tag)
                .join(tag.postTags, postTag)
                .groupBy(tag.id)
                .orderBy(tag.id.count().desc())
                .limit(limit)
                .fetch();
    }
}
