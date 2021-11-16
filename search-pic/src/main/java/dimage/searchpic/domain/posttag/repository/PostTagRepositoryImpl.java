package dimage.searchpic.domain.posttag.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dimage.searchpic.dto.tag.TagDto;
import lombok.RequiredArgsConstructor;
import java.util.List;
import static dimage.searchpic.domain.post.QPost.*;
import static dimage.searchpic.domain.posttag.QPostTag.postTag;
import static dimage.searchpic.domain.tag.QTag.tag;

@RequiredArgsConstructor
public class PostTagRepositoryImpl implements PostTagRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<TagDto> getTopTags(long location_id, int limit) {
        return queryFactory
                .select(Projections.constructor(TagDto.class, tag.id, tag.name))
                .from(postTag)
                .join(postTag.post, post)
                .join(postTag.tag, tag)
                .where(post.location.id.eq(location_id))
                .groupBy(tag.id)
                .orderBy(tag.id.count().desc())
                .limit(limit)
                .fetch();
    }
}