package dimage.searchpic.controller;

import dimage.searchpic.config.auth.CurrentMember;
import dimage.searchpic.domain.member.Member;
import dimage.searchpic.dto.common.CommonInfo;
import dimage.searchpic.dto.common.CommonResponse;
import dimage.searchpic.dto.member.MemberResponse;
import dimage.searchpic.dto.member.NicknameChangeRequest;
import dimage.searchpic.exception.ErrorResponse;
import dimage.searchpic.service.MemberService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import javax.validation.Valid;
import java.net.MalformedURLException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @ApiOperation(value = "프로필 정보 변경")
    @PostMapping(value = "/profile")
    @ApiResponses({
            @ApiResponse(code = 200, response = CommonResponse.class,message = "프로필 변경 성공"),
            @ApiResponse(code = 400, response = ErrorResponse.class, message = "닉네임을 한 글자 이상 입력해야 합니다.")
    })
    public ResponseEntity<?> changeProfile(@ApiParam(value = "프로필 이미지") @RequestPart(value = "file", required = false) MultipartFile multipartFile,
                                           @ApiParam(value = "닉네임 값을 갖는 JSON 데이터", required = true) @RequestPart(value = "data") @Valid NicknameChangeRequest changeRequest,
                                           @ApiIgnore @CurrentMember Member member) {
        memberService.updateProfile(changeRequest, multipartFile, member.getId());
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS));
    }

    @ApiOperation(value = "사용자 정보 조회")
    @ApiResponses({
            @ApiResponse(code = 200, response = MemberResponse.class, message = "조회 성공")
    })
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@ApiIgnore @CurrentMember Member member) {
        MemberResponse memberResponse = memberService.getProfileInfo(member.getId());
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS, memberResponse));
    }

    @ApiOperation(value = "로컬에서 사용자 프로필 이미지 조회")
    @GetMapping(value = "/image", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getImage(@ApiParam(value = "프로필 이미지",required = true) @RequestParam(value = "imgurl") String imageUrl) throws MalformedURLException {
        Resource resource = new UrlResource("file:" + imageUrl);
        return ResponseEntity.ok()
                .body(resource);
    }
}