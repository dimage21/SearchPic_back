package dimage.searchpic.integration;

import dimage.searchpic.config.auth.JwtTokenProvider;
import dimage.searchpic.domain.member.Member;
import dimage.searchpic.domain.member.repository.MemberRepository;
import dimage.searchpic.domain.token.repository.RedisTokenRepository;
import dimage.searchpic.exception.auth.ExpiredTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RedisTokenRepository redisTokenRepository;

    private String sampleRefreshToken;
    private Member member;

    // 샘플 유저를 저장하고, 유저 아이디로 발급한 샘플 리프레시 토큰 값을 레디스 저장소에 저장한다.
    @BeforeEach
    void setup() {
        memberRepository.deleteAll();
        member = Member.builder()
                .nickname("test")
                .email("test@naver.com")
                .build();
        memberRepository.save(member);
        sampleRefreshToken = jwtTokenProvider.createAccessAndRefreshTokens(member.getId().toString()).getRefreshToken();

        long refreshTokenValidTime = 1000L * 60 * 30; // sample valid time: 30분
        redisTokenRepository.setRefreshTokenWithExpireTime(member.getId().toString(),sampleRefreshToken, refreshTokenValidTime);
    }


    @DisplayName("헤더에 리프레시 토큰을 담아, 토큰 재발급 요청을 보내면 액세스 토큰과 리프레시 토큰을 새로 발급하여 반환한다.")
    @Test
    public void 액세스_토큰_재발급_성공() throws Exception {
        // given
        final String headerName = "refresh-token";
        Thread.sleep(1000);
        // when
        mockMvc.perform(get("/reissue/access-token")
                        .header(headerName, sampleRefreshToken))
                        .andExpect(status().isOk());

        String savedRefreshToken = redisTokenRepository.getRefreshToken(member.getId().toString());
        // then
        assertThat(savedRefreshToken).isNotEqualTo(sampleRefreshToken);
    }

    @DisplayName("잘못된 리프레시 토큰을 헤더에 담아 요청할 경우, 예외가 발생한다.")
    @Test
    public void 액세스_토큰_재발급_실패() throws Exception {
        // given
        final String wrongTokenValue = "wrongToken";
        final String headerName = "refresh-token";
        // when
        mockMvc.perform(get("/reissue/access-token")
                .header(headerName, wrongTokenValue))
                .andExpect(status().is4xxClientError());
        String savedRefreshToken = redisTokenRepository.getRefreshToken(member.getId().toString());
        // then
        assertThat(savedRefreshToken).isEqualTo(sampleRefreshToken);
    }

    @DisplayName("헤더에 리프레시 토큰을 담아, 로그아웃 요청을 보내면 저장된 리프레시 토큰을 삭제한다.")
    @Test
    public void 로그아웃_성공() throws Exception {
        // given
        final String headerName = "refresh-token";

        // when
        mockMvc.perform(delete("/logout")
                        .header(headerName, sampleRefreshToken))
                        .andExpect(status().isOk());
        // then
        assertThrows(ExpiredTokenException.class, () -> {
            redisTokenRepository.getRefreshToken(member.getId().toString());
        });
    }

    @DisplayName("잘못된 리프레시 토큰을 헤더에 담아, 로그아웃 요청을 보내면 예외가 발생한다.")
    @Test
    public void 로그아웃_실패() throws Exception {
        // given
        final String wrongTokenValue = "wrongToken";
        final String headerName = "refresh-token";

        // when
        ResultActions response = mockMvc.perform(delete("/logout")
                        .header(headerName, wrongTokenValue))
                        .andExpect(status().is4xxClientError());

        String savedRefreshToken = redisTokenRepository.getRefreshToken(member.getId().toString());
        // then
        assertThat(savedRefreshToken).isEqualTo(sampleRefreshToken);
    }
}
