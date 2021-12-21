package dimage.searchpic.service;

import dimage.searchpic.domain.location.Location;
import dimage.searchpic.domain.location.repository.LocationRepository;
import dimage.searchpic.domain.member.Member;
import dimage.searchpic.domain.member.repository.MemberRepository;
import dimage.searchpic.domain.post.Post;
import dimage.searchpic.domain.post.repository.PostRepository;
import dimage.searchpic.domain.posttag.PostTag;
import dimage.searchpic.domain.tag.Tag;
import dimage.searchpic.dto.post.PostRequest;
import dimage.searchpic.dto.post.PostResponse;
import dimage.searchpic.dto.tag.SearchOrder;
import dimage.searchpic.exception.CustomException;
import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.common.NotFoundException;
import dimage.searchpic.exception.post.BadAccessException;
import dimage.searchpic.util.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final TagService tagService;
    private final LocationService locationService;

    private final FileStorage fileStorage;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final LocationRepository locationRepository;

    public PostResponse savePost(MultipartFile multipartFile, PostRequest postRequest, Long memberId, double x, double y) {
        List<Tag> tags = tagService.getTags(postRequest.getTags());

        String url = fileStorage.storeFile(multipartFile, memberId);
        Location location = locationService.findOrCreate(url,x, y);
        Member author = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ErrorInfo.MEMBER_NULL));

        Post newPost = Post.builder()
                .author(author)
                .location(location)
                .pictureUrl(url)
                .description(postRequest.getMemo())
                .build();

        List<PostTag> postTags = tags.stream()
                .map(tag -> new PostTag(newPost, tag))
                .collect(Collectors.toList());
        newPost.setPostTags(postTags);

        postRepository.save(newPost);
        return PostResponse.of(newPost);
    }

    public PostResponse updatePost(PostRequest updateRequest, Long memberId, Long postId) {
        Post post = postRepository.getPostByAuthorAndId(postId, memberId).orElseThrow(() -> new BadAccessException(ErrorInfo.NOT_ALLOWED_ACCESS));
        List<Tag> tags = tagService.getTags(updateRequest.getTags());

        List<PostTag> postTags = tags.stream()
                .map(tag -> new PostTag(post, tag))
                .collect(Collectors.toList());
        post.updatePost(postTags,updateRequest.getMemo());
        return PostResponse.of(post);
    }

    public void deletePost(Long postId, Long memberId) {
        // 해당 멤버가 해당 글을 작성했는지 확인
        Post post = postRepository.getPostByAuthorAndId(postId, memberId).orElseThrow(() -> new BadAccessException(ErrorInfo.NOT_ALLOWED_ACCESS));
        post.delete();
        postRepository.delete(post); // 글과 글에 등록된 태그 모두 같이 삭제
    }

    // 본인이 조회한 것이 아니라면 조회 수가 1 증가한다.
    public PostResponse findPostAndUpdateView(Long postId, Long memberId) {
        Post post = postRepository.findOnePostById(postId).orElseThrow(() -> new NotFoundException(ErrorInfo.POST_NULL));
        if (!post.getAuthor().getId().equals(memberId)) // 작성자가 글을 조회한 게 아니라면 조회수 1 증가
            post.addView();
        return PostResponse.of(post);
    }

    @Transactional(readOnly = true)
    public List<Location> getNearSpotPosts(Long locationId, double distance, PageRequest pageRequest) {
        Location targetLocation = locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException(ErrorInfo.LOCATION_NULL));
        return locationRepository.nearSpotsFromPlace(targetLocation.getX(), targetLocation.getY(), distance, pageRequest.getOffset(),pageRequest.getPageSize()); // distance km 이내에 위치한 장소
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getNearSpotsPostLimit(Long locationId, double distance) {
        List<Location> nearLocations = getNearSpotPosts(locationId, distance, PageRequest.of(0, 4));

        return nearLocations.stream().filter(
                location-> location.getPosts().size() > 0
        )
        .map(result -> PostResponse.of(result.getPosts().get(0)))
        .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getFilteredPosts(List<String> searchTagNames, Pageable pageable, SearchOrder order) {
        if (searchTagNames.size() > 5)
            throw new CustomException(ErrorInfo.MAX_TAG_COUNT_LIMIT);
        List<Post> filteredPosts = postRepository.getFilteredPosts(searchTagNames, pageable.getOffset(), pageable.getPageSize(),order);
        return filteredPosts.stream().map(PostResponse::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsMemberWrite(Long memberId, Pageable pageable) {
        return postRepository.getPostsMemberWrite(memberId, pageable.getOffset(), pageable.getPageSize());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsOnSpot(Long locationId) {
        Location location = locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException(ErrorInfo.LOCATION_NULL));
        return postRepository.findByLocationOrderByCreatedDateDesc(location)
                .stream().map(PostResponse::of).collect(Collectors.toList());
    }
}