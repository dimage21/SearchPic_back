package dimage.searchpic.controller;

import dimage.searchpic.domain.member.Member;
import dimage.searchpic.service.auth.AuthService;
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
    private final AuthService authService;

    @GetMapping(value = "/login/oauth2/code/{provider}")
    public ResponseEntity<?> getCode(@RequestParam(value = "code", required = false) String code, @PathVariable String provider) {
        log.info("[AuthController] getCode is called, code = {}", code);
        String token = authService.getTokenFromProvider(code, provider);

        //TODO: Create Token & response Token
        Member member = authService.getMemberByAccessToken(token, provider);

        return ResponseEntity.ok(null);
    }
}
