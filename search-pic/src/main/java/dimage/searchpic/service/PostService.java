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
import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.common.NotFoundException;
import dimage.searchpic.exception.post.BadAccessException;
import dimage.searchpic.util.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public void deletePost(Long postId, Long memberId) {
        // 해당 멤버가 해당 글을 작성했는지 확인
        Post post = postRepository.getPostByAuthorAndId(postId, memberId).orElseThrow(() -> new BadAccessException(ErrorInfo.NOT_ALLOWED_ACCESS));
        post.delete();
        postRepository.delete(post); // 글과 글에 등록된 태그 모두 같이 삭제
    }

    public PostResponse findPost(Long postId, Long memberId) {
        Post post = postRepository.findOnePostById(postId).orElseThrow(() -> new NotFoundException(ErrorInfo.POST_NULL));
        if (!post.getAuthor().getId().equals(memberId)) // 작성자가 글을 조회한 게 아니라면 조회수 1 증가
            post.addView();
        return PostResponse.of(post);
    }

    public List<PostResponse> getNearSpotsPost(Long locationId, double distance) {
        Location targetLocation = locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException(ErrorInfo.LOCATION_NULL));
        List<Location> nearLocations = locationRepository.getNearLocations(targetLocation.getX(), targetLocation.getY(), distance); // distance km 이내에 위치한 장소
        return nearLocations.stream().filter(
                location-> location.getPosts().size() > 0
        )
        .map(result -> PostResponse.of(result.getPosts().get(0)))
        .collect(Collectors.toList());
    }
}