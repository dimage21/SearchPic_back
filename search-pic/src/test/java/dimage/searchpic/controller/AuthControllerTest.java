package dimage.searchpic.controller;


import dimage.searchpic.config.auth.JwtTokenProvider;
import dimage.searchpic.dto.auth.TokenResponse;
import dimage.searchpic.service.auth.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("소셜사에게 받은 토큰을 파라미터로, 소셜사 이름을 URL 경로에 넣어 요청한다면, 액세스 토큰과 리프레시 토큰을 새로 발급하여 반환한다.")
    @Test
    public void 소셜_로그인_성공() throws Exception {
        // given
        final String accessTokenFromSocialProvider = "test";
        final String providerName = "naver";
        final String paramName = "token";
        final TokenResponse tokenResponse = TokenResponse.of("newAccessToken", "newRefreshToken");
        given(authService.loginOrSignUpAndCreateTokens(accessTokenFromSocialProvider, providerName))
                .willReturn(tokenResponse);
        // when
        ResultActions response = mockMvc.perform(get("/login/{provider}", providerName)
                .param(paramName, accessTokenFromSocialProvider));
        // then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken", is(tokenResponse.getAccessToken())))
                .andExpect(jsonPath("$.data.refreshToken", is(tokenResponse.getRefreshToken())));
    }

    @DisplayName("헤더에 리프레시 토큰을 담아, 토큰 재발급 요청을 보내면 액세스 토큰과 리프레시 토큰을 새로 발급하여 반환한다.")
    @Test
    public void 액세스_토큰_재발급_성공() throws Exception {
        // given
        final String pastRefreshToken = "pastRefreshToken";
        final TokenResponse tokenResponse = TokenResponse.of("newAccessToken", "newRefreshToken");
        final String headerName = "refresh-token";
        given(authService.reissue(pastRefreshToken)).willReturn(tokenResponse);
        // when
        ResultActions response = mockMvc.perform(get("/reissue/access-token")
                        .header(headerName, pastRefreshToken));
        // then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken", is(tokenResponse.getAccessToken())))
                .andExpect(jsonPath("$.data.refreshToken", is(tokenResponse.getRefreshToken())));
    }

    @DisplayName("헤더에 리프레시 토큰을 담아, 로그아웃 요청을 보내면 로그아웃을 진행한다.")
    @Test
    public void 로그아웃_성공() throws Exception {
        // given
        final String pastRefreshToken = "pastRefreshToken";
        final String headerName = "refresh-token";
        willDoNothing().given(authService).logout(pastRefreshToken);

        // when
        ResultActions response = mockMvc.perform(delete("/logout")
                .header(headerName, pastRefreshToken));
        // then
        response.andDo(print())
                .andExpect(status().isOk());
    }
}