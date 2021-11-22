package dimage.searchpic.domain.post.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dimage.searchpic.domain.post.Post;
import dimage.searchpic.dto.post.PostResponse;
import dimage.searchpic.dto.posttag.PostTagDto;
import dimage.searchpic.dto.tag.SearchOrder;
import lombok.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    @Override
    public List<PostResponse> getPostsMemberWrite(Long memberId, long offset, int size) {
        List<PostResponse> postResponses = getPostResponses(memberId, offset, size);
        Map<Long, List<PostTagDto>> postTagsMap = findTagsForPosts(getPostIds(postResponses));

        postResponses.forEach(p -> {
            List<String> tagNames = postTagsMap.get(p.getPostId())
                    .stream()
                    .map(PostTagDto::getTagName)
                    .collect(Collectors.toList());
            p.setTagNames(tagNames);
        });
        return postResponses;
    }

    private List<Long> getPostIds(List<PostResponse> postResponses) {
        return postResponses.stream()
                .map(PostResponse::getPostId)
                .collect(Collectors.toList());
    }

    private Map<Long, List<PostTagDto>> findTagsForPosts(List<Long> postIds) {
        List<PostTagDto> fetchTags = queryFactory
                .select(
                        Projections.fields(PostTagDto.class,
                                tag.name.as("tagName"),
                                tag.id.as("tagId"),
                                postTag.post.id.as("postId"))
                )
                .from(postTag)
                .join(postTag.tag, tag)
                .where(postTag.post.id.in(postIds))
                .fetch();

        return fetchTags.stream()
                .collect(Collectors.groupingBy(PostTagDto::getPostId));
    }

    private List<PostResponse> getPostResponses(Long memberId, long offset, int size){
        return queryFactory
                .select(Projections.constructor(PostResponse.class,
                        post.id.as("postId"),
                        post.pictureUrl,
                        location.address,
                        post.description,
                        location.id.as("locationId")
                ))
                .from(post)
                .join(post.location, location)
                .where(post.author.id.eq(memberId))
                .orderBy(post.createdDate.desc())
                .offset(offset)
                .limit(size)
                .fetch();
    }
}
