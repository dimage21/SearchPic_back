package dimage.searchpic.controller;

import dimage.searchpic.config.auth.CurrentMember;
import dimage.searchpic.domain.member.Member;
import dimage.searchpic.dto.common.CommonInfo;
import dimage.searchpic.dto.common.CommonResponse;
import dimage.searchpic.dto.post.PostRequest;
import dimage.searchpic.dto.post.PostResponse;
import dimage.searchpic.dto.tag.SearchOrder;
import dimage.searchpic.service.PostService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @ApiOperation(value = "글 등록")
    @PostMapping("/post")
    @ApiResponses({
            @ApiResponse(code = 200, response = PostResponse.class, message = "등록 성공")
    })
    public ResponseEntity<?> createPost(@ApiIgnore @CurrentMember Member member,
                                        @RequestParam double x, @RequestParam double y,
                                        @RequestPart(value = "image") MultipartFile multipartFile,
                                        @RequestPart(value = "data") PostRequest postRequest){

        PostResponse response = postService.savePost(multipartFile, postRequest, member.getId(), x, y);
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS,response));
    }

    @ApiOperation(value = "글 삭제")
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<?> deletePost(@ApiIgnore @CurrentMember Member member,
                                        @PathVariable Long postId) {
        postService.deletePost(postId,member.getId());
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS));
    }

    @ApiOperation(value = "글 수정")
    @PatchMapping("/post/{postId}")
    public ResponseEntity<?> modifyPost(@ApiIgnore @CurrentMember Member member,
                                        @PathVariable Long postId,
                                        @RequestBody PostRequest postUpdateRequest) {
        PostResponse response = postService.updatePost(postUpdateRequest, member.getId(), postId);
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS, response));
    }

    @ApiOperation(value = "글 조회")
    @ApiResponses({
            @ApiResponse(code = 200, response = PostResponse.class, message = "조회 성공")
    })
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getPost(@ApiIgnore  @CurrentMember Member member, @PathVariable Long postId) {
        PostResponse response = postService.findPostAndUpdateView(postId, member.getId());
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS,response));
    }

    @ApiOperation("특정 장소 근처 포토 스팟 글 조회")
    @ApiResponses({
            @ApiResponse(code = 200, response = PostResponse.class, message = "조회 성공")
    })
    @GetMapping(value = {"/{locationId}/posts/{distance}", "{locationId}/posts"})
    public ResponseEntity<?> getNearSpotRepresentInfo(@PathVariable(value = "locationId") Long locationId,
                                             @PathVariable(value = "distance",required = false) Optional<Double> distanceKm) {
        double distance = distanceKm.orElse(1.0);
        List<PostResponse> response = postService.getNearSpotsPostLimit(locationId, distance);
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS,response));
    }

    @ApiOperation("요청한 태그로 필터한 여러 게시글을 반환")
    @ApiResponses({
            @ApiResponse(code = 200, response = PostResponse.class, message = "조회 성공")
    })
    @GetMapping("/posts/search")
    public ResponseEntity<?> getPostsFilterByTags(@RequestParam("tags") List<String> tags,
                                                  @RequestParam("order") SearchOrder searchOrder,
                                                  final Pageable pageable) {
        List<PostResponse> response = postService.getFilteredPosts(tags, pageable,searchOrder);
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS, response));
    }

    @ApiOperation("특정 유저가 작성한 여러 게시글을 생성 순으로 반환")
    @ApiResponses({
            @ApiResponse(code = 200, response = PostResponse.class, message = "조회 성공")
    })
    @GetMapping("/posts/member")
    public ResponseEntity<?> getPostsByMember(@ApiIgnore @CurrentMember Member member,
                                              final Pageable pageable) {
        List<PostResponse> response = postService.getPostsMemberWrite(member.getId(), pageable);
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS, response));
    }

    @ApiOperation("특정 장소에 등록된 여러 게시글을 생성 순으로 반환")
    @GetMapping("/posts/{locationId}")
    public ResponseEntity<?> getPostsByLocation(@PathVariable(value = "locationId") Long locationId){
        List<PostResponse> response = postService.getPostsOnSpot(locationId);
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS,response));
    }
}
