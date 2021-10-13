package dimage.searchpic.controller;

import dimage.searchpic.dto.auth.TokenResponse;
import dimage.searchpic.dto.common.CommonInfo;
import dimage.searchpic.dto.common.CommonResponse;
import dimage.searchpic.service.MemberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    @ApiOperation(value = "소셜 로그인")
    @ApiResponses({
            @ApiResponse(code = 200, response = TokenResponse.class,message = "서버에서 생성한 액세스 토큰을 리턴한다.")
    })
    @GetMapping(value = "/login/{provider}")
    public ResponseEntity<?> getTokenFromClient(@ApiParam(value = "소셜사에게 받은 액세스 토큰" ,required = true) @RequestParam(value = "token") String token,
                                                @ApiParam(value = "소셜사 이름",required = true) @PathVariable String provider) {
        log.info("provider = {}",provider);
        log.info("token = {}",token);
        TokenResponse accessToken = TokenResponse.of(memberService.createToken(token, provider));
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.LOGIN_SUCCESS,accessToken));
    }
}