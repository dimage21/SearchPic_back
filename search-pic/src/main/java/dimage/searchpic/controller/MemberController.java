package dimage.searchpic.controller;

import dimage.searchpic.config.auth.CurrentMember;
import dimage.searchpic.domain.member.Member;
import dimage.searchpic.dto.common.CommonInfo;
import dimage.searchpic.dto.common.CommonResponse;
import dimage.searchpic.dto.member.MemberResponse;
import dimage.searchpic.dto.member.NicknameChangeRequest;
import dimage.searchpic.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.net.MalformedURLException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/profile")
    public ResponseEntity<?> changeProfile(@RequestPart(value = "file", required = false) MultipartFile multipartFile,
                                           @RequestPart(value = "data", required = false) @Valid NicknameChangeRequest changeRequest,
                                           @CurrentMember Member member) {
        memberService.updateProfile(changeRequest, multipartFile, member.getId());
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@CurrentMember Member member) {
        MemberResponse memberResponse = memberService.getProfileInfo(member.getId());
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS, memberResponse));
    }

    @GetMapping(value = "/image", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getImage(@RequestParam(value = "imgurl") String imageUrl) throws MalformedURLException {
        Resource resource = new UrlResource("file:" + imageUrl);
        return ResponseEntity.ok()
                .body(resource);
    }
}