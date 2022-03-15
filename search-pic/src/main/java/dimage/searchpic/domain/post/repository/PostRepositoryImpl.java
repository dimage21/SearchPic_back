package dimage.searchpic.domain.post.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dimage.searchpic.dto.location.MarkLocationResponse;
import dimage.searchpic.dto.post.PostResponse;
import dimage.searchpic.dto.posttag.PostTagDto;
import dimage.searchpic.dto.tag.SearchOrder;
import lombok.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static dimage.searchpic.domain.location.QLocation.location;
import static dimage.searchpic.domain.member.QMember.member;
import static dimage.searchpic.domain.post.QPost.post;
import static dimage.searchpic.domain.posttag.QPostTag.postTag;
import static dimage.searchpic.domain.tag.QTag.tag;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<PostResponse> getFilteredPosts(List<String> tagNames, long offset, int size, SearchOrder order) {
        JPAQuery<PostResponse> postJPAQuery = queryFactory
                .select(Projections.constructor(PostResponse.class,
                        post.id.as("id"),
                        post.pictureUrl,
                        location.address,
                        post.description,
                        location.id.as("locationId")
                        ))
                .from(post)
                .join(post.location, location)
                .join(post.postTags, postTag)
                .join(postTag.tag, tag)
                .where(tag.name.in(tagNames))
                .groupBy(post.id)
                .having(tag.count().goe(tagNames.size()))
                .offset(offset)
                .limit(size)
                .orderBy(sort(order));

        List<PostResponse> postResponses = postJPAQuery.fetch();
        Map<Long, List<PostTagDto>> tagsForPosts = findTagsForPosts(getPostIds(postResponses));
        insertTagNamesPerPost(postResponses, tagsForPosts);
        return postResponses;
    }

    private OrderSpecifier<?> sort(SearchOrder order) {
        OrderSpecifier<?> orderCondition = null;
        switch (order){
            case VIEW:
                orderCondition = post.view.desc();
                break;
            case RECENT:
                orderCondition = post.createdDate.desc();
                break;
            default:
                break;
        }
        return orderCondition;
    }

    @Override
    public List<PostResponse> getPostsMemberWrite(Long memberId, long offset, int size) {
        List<PostResponse> postResponses = getPostResponses(memberId, offset, size);
        Map<Long, List<PostTagDto>> postTagsMap = findTagsForPosts(getPostIds(postResponses));
        insertTagNamesPerPost(postResponses, postTagsMap);
        return postResponses;
    }

    private List<Long> getPostIds(List<PostResponse> postResponses) {
        return postResponses.stream()
                .map(PostResponse::getId)
                .collect(Collectors.toList());
    }

    private void insertTagNamesPerPost(List<PostResponse> postResponses, Map<Long, List<PostTagDto>> postTagsMap) {
        postResponses.forEach(p -> {
            if (postTagsMap.containsKey(p.getId())){
                List<String> tagNames = postTagsMap.get(p.getId())
                        .stream()
                        .map(PostTagDto::getTagName)
                        .collect(Collectors.toList());
                p.setTagNames(tagNames);
            }
        });
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

    @Override
    public List<MarkLocationResponse> findLocationsMemberWrite(Long memberId) {
        return queryFactory
                .select(Projections.fields(MarkLocationResponse.class,
                        location.id.as("id"),
                        location.repImageUrl.as("imageUrl"),
                        location.placeName.as("placeName"),
                        location.address.as("address"),
                        location.x.as("x"),
                        location.y.as("y")
                ))
                .from(post)
                .leftJoin(post.location, location)
                .leftJoin(post.author, member)
                .where(member.id.eq(memberId))
                .fetch();
    }
}
