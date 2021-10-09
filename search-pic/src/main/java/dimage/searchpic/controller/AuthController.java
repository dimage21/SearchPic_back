package dimage.searchpic.controller;

import dimage.searchpic.dto.auth.TokenResponse;
import dimage.searchpic.dto.common.CommonInfo;
import dimage.searchpic.dto.common.CommonResponse;
import dimage.searchpic.service.auth.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final MemberService memberService;

    @GetMapping(value = "/login/oauth2/code/{provider}")
    public ResponseEntity<?> getCode(@RequestParam(value = "code", required = false) String code, @PathVariable String provider) {
        log.info("[AuthController] getCode is called, code = {}", code);
        String token = memberService.getTokenFromProvider(code, provider);

        TokenResponse accessToken = TokenResponse.of(memberService.createToken(token, provider));
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.LOGIN_SUCCESS,accessToken));
    }
}