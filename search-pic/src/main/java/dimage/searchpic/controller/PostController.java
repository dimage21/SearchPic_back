package dimage.searchpic.controller;

import dimage.searchpic.config.auth.CurrentMember;
import dimage.searchpic.domain.member.Member;
import dimage.searchpic.dto.common.CommonInfo;
import dimage.searchpic.dto.common.CommonResponse;
import dimage.searchpic.dto.post.PostRequest;
import dimage.searchpic.dto.post.PostResponse;
import dimage.searchpic.service.PostService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import javax.validation.Valid;

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
                                        @RequestPart(value = "data") @Valid PostRequest postRequest){

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

    @ApiOperation(value = "글 조회")
    @ApiResponses({
            @ApiResponse(code = 200, response = PostResponse.class, message = "조회 성공")
    })
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getPost(@ApiIgnore  @CurrentMember Member member, @PathVariable Long postId) {
        PostResponse response = postService.findPost(postId, member.getId());
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS,response));
    }
}
