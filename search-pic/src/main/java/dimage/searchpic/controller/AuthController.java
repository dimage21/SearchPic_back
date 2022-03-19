package dimage.searchpic.controller;

import dimage.searchpic.dto.auth.LoginInfoRequest;
import dimage.searchpic.dto.auth.TokenResponse;
import dimage.searchpic.dto.common.CommonInfo;
import dimage.searchpic.dto.common.CommonResponse;
import dimage.searchpic.service.auth.AuthService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @ApiOperation(value = "소셜 로그인")
    @ApiResponses({
            @ApiResponse(code = 200, response = TokenResponse.class,message = "서버에서 생성한 액세스 토큰과 리프레시 토큰을 리턴한다.")
    })
    @PostMapping(value = "/login")
    public ResponseEntity<CommonResponse<TokenResponse>> login(@RequestBody LoginInfoRequest loginInfo) {
        TokenResponse response = authService.loginOrSignUpAndCreateTokens(loginInfo);
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.LOGIN_SUCCESS,response));
    }

    @ApiOperation(value = "액세스 토큰 재발급")
    @ApiResponses({
            @ApiResponse(code = 200, response = TokenResponse.class,message = "리프레시 토큰을 사용해서, 새로운 액세스 토큰과 새로운 리프레시 토큰을 발급한 후 함께 리턴한다.")
    })
    @GetMapping(value = "/reissue/access-token")
    public ResponseEntity<?> reissueAccessToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("refresh-token");
        System.out.println("refreshToken = " + refreshToken);
        TokenResponse response = authService.reissue(refreshToken);
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS,response));
    }

    @ApiOperation(value = "로그아웃")
    @DeleteMapping(value = "/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String refreshToken = request.getHeader("refresh-token");
        authService.logout(refreshToken);
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS));
    }
}
