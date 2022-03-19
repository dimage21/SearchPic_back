package dimage.searchpic.service.auth;

import dimage.searchpic.config.auth.JwtTokenProvider;
import dimage.searchpic.domain.member.Member;
import dimage.searchpic.domain.member.Provider;
import dimage.searchpic.domain.member.ProviderName;
import dimage.searchpic.domain.member.repository.MemberRepository;
import dimage.searchpic.domain.token.repository.RedisTokenRepository;
import dimage.searchpic.dto.auth.LoginInfoRequest;
import dimage.searchpic.dto.auth.TokenResponse;
import dimage.searchpic.exception.auth.BadTokenException;
import dimage.searchpic.exception.auth.UnauthorizedMemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    RedisTokenRepository redisTokenRepository;
    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    AuthService authService;

    private Member member;

    private LoginInfoRequest loginInfoRequest;

    @BeforeEach
    public void setup() {
        member = Member.builder()
                .email("test@naver.com")
                .id(1L)
                .provider(new Provider("1111", ProviderName.NAVER))
                .build();

        final String providerId = UUID.randomUUID().toString();
        final String userEmail = "test@naver.com";
        loginInfoRequest = new LoginInfoRequest(new Provider(providerId, ProviderName.NAVER), userEmail);
    }

    @DisplayName("로그인 및 회원가입 요청 - 이미 회원가입한 회원이 소셜 로그인할 경우 리프레시 토큰과 액세스 토큰을 담은 객체를 반환한다.")
    @Test
    public void login_currentMember() throws Exception {
        // given
        final UserInfo userInfo = UserInfo.builder().id("1111").email("test@naver.com").build();
        final TokenResponse tokenResponse = TokenResponse.of("sampleAccess", "sampleRefresh");

        given(memberRepository.findByProviderId(anyString(), any(ProviderName.class))).willReturn(Optional.of(member));
        given(jwtTokenProvider.createAccessAndRefreshTokens(eq(member.getId().toString()))).willReturn(tokenResponse);
        willDoNothing().given(redisTokenRepository).setRefreshTokenWithExpireTime(eq(member.getId().toString()),anyString(),anyLong());

        // when
        TokenResponse tokenResponseResult = authService.loginOrSignUpAndCreateTokens(loginInfoRequest);

        // then
        assertThat(tokenResponseResult.getAccessToken()).isEqualTo(tokenResponseResult.getAccessToken());
        assertThat(tokenResponseResult.getRefreshToken()).isEqualTo(tokenResponseResult.getRefreshToken());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @DisplayName("로그인 및 회원가입 요청 - 새로운 회원이 소셜 로그인할 경우 회원가입을 진행하고 리프레시 토큰과 액세스 토큰을 담은 객체를 반환한다.")
    @Test
    public void login_newMember() throws Exception {
        // given
        final TokenResponse tokenResponse = TokenResponse.of("sampleAccess", "sampleRefresh");

        given(memberRepository.findByProviderId(anyString(), any(ProviderName.class))).willReturn(Optional.empty());
        given(jwtTokenProvider.createAccessAndRefreshTokens(anyString())).willReturn(tokenResponse);
        given(memberRepository.save(any(Member.class))).willReturn(member);
        willDoNothing().given(redisTokenRepository).setRefreshTokenWithExpireTime(anyString(), anyString(), anyLong());

        // when
        TokenResponse tokenResponseResult = authService.loginOrSignUpAndCreateTokens(loginInfoRequest);

        // then
        assertThat(tokenResponseResult.getAccessToken()).isEqualTo(tokenResponseResult.getAccessToken());
        assertThat(tokenResponseResult.getRefreshToken()).isEqualTo(tokenResponseResult.getRefreshToken());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @DisplayName("토큰 재발급 성공 요청 - 가장 최근에 발급한 리프레시 토큰을 가지고 액세스 토큰을 재발급한다면, 새로운 액세스 토큰과 새로운 리프레시 토큰을 발급하여 반환한다.")
    @Test
    public void reissue_accessToken_with_correct_refreshToken() throws Exception {
        // given
        final TokenResponse tokenResponse = TokenResponse.of("sampleAccessToken", "sampleRefreshToken");
        final String sampleRefreshToken = "test";
        given(jwtTokenProvider.isValidToken(anyString(), anyString())).willReturn(true);
        given(jwtTokenProvider.getPkFromToken(anyString(),anyString())).willReturn(member.getId().toString());
        given(jwtTokenProvider.createAccessAndRefreshTokens(eq(member.getId().toString()))).willReturn(tokenResponse);
        willDoNothing().given(redisTokenRepository).validIsRecentAndUsableToken(eq(member.getId().toString()), anyString());
        willDoNothing().given(redisTokenRepository).setRefreshTokenWithExpireTime(eq(member.getId().toString()),anyString(), anyLong());
        // when
        TokenResponse tokenResponseResult = authService.reissue(sampleRefreshToken);
        // then
        assertThat(tokenResponseResult.getAccessToken()).isEqualTo(tokenResponseResult.getAccessToken());
        assertThat(tokenResponseResult.getRefreshToken()).isEqualTo(tokenResponseResult.getRefreshToken());
        assertThat(sampleRefreshToken).isNotEqualTo(tokenResponseResult.getRefreshToken());
    }

    @DisplayName("토큰 재발급 실패 요청 - 가장 최근에 발급하지 않은 리프레시 토큰을 가지고 액세스 토큰을 재발급한다면, 유효한 토큰이 필요하다는 예외(UnauthorizedMemberException)가 발생한다.")
    @Test
    public void reissue_accessToken_with_wrong_refreshToken() throws Exception {
        // given
        final String sampleRefreshToken = "test";
        given(jwtTokenProvider.isValidToken(anyString(), anyString())).willReturn(true);
        given(jwtTokenProvider.getPkFromToken(anyString(),anyString())).willReturn(member.getId().toString());
        willThrow(UnauthorizedMemberException.class)
                .given(redisTokenRepository).validIsRecentAndUsableToken(eq(member.getId().toString()), anyString());

        // when
        assertThrows(UnauthorizedMemberException.class, () -> {
            authService.reissue(sampleRefreshToken);
        });

        // then
        verify(jwtTokenProvider, never()).createAccessAndRefreshTokens(anyString());
        verify(redisTokenRepository, never()).setRefreshTokenWithExpireTime(anyString(), anyString(), anyLong());
    }

    @DisplayName("토큰 재발급 실패 요청 - 유효하지 않은 리프레시 토큰으로 요청을 보낼 경우 BadTokenException 예외가 발생한다.")
    @Test
    public void reissue_accessToken_with_bad_refreshToken() throws Exception {
        // given
        final String sampleRefreshToken = "test";
        willThrow(BadTokenException.class)
                .given(jwtTokenProvider).isValidToken(anyString(), anyString());

        // when
        assertThrows(BadTokenException.class, () -> {
            authService.reissue(sampleRefreshToken);
        });

        // then
        verify(jwtTokenProvider, never()).getPkFromToken(anyString(), anyString());
        verify(redisTokenRepository, never()).validIsRecentAndUsableToken(anyString(), anyString());
        verify(jwtTokenProvider, never()).createAccessAndRefreshTokens(anyString());
        verify(redisTokenRepository, never()).setRefreshTokenWithExpireTime(anyString(), anyString(), anyLong());
    }

    @DisplayName("로그아웃 정상 요청 - 성공")
    @Test
    public void logout_with_recent_refreshToken() throws Exception {
        // given
        final String sampleRefreshToken = "test";
        given(jwtTokenProvider.isValidToken(anyString(), anyString())).willReturn(true);
        given(jwtTokenProvider.getPkFromToken(anyString(),anyString())).willReturn(member.getId().toString());
        willDoNothing().given(redisTokenRepository).deleteRefreshToken(anyString(), anyString());
        // when
        authService.logout(sampleRefreshToken);
        // then
        verify(redisTokenRepository, times(1)).deleteRefreshToken(anyString(), anyString());
    }

    @DisplayName("로그아웃 실패 요청 - 가장 최근에 발급하지 않은 리프레시 토큰을 가지고 로그아웃을 요청한다면, 유효한 토큰이 필요하다는 예외(UnauthorizedMemberException)가 발생한다.")
    @Test
    public void logout_with_past_refreshToken() throws Exception {
        final String sampleRefreshToken = "test";
        given(jwtTokenProvider.isValidToken(anyString(), anyString())).willReturn(true);
        given(jwtTokenProvider.getPkFromToken(anyString(), anyString())).willReturn(member.getId().toString());
        willThrow(UnauthorizedMemberException.class)
                .given(redisTokenRepository).deleteRefreshToken(eq(member.getId().toString()), anyString());
        // when
        assertThrows(UnauthorizedMemberException.class, () -> {
            authService.logout(sampleRefreshToken);
        });
        // then
        verify(redisTokenRepository, times(1)).deleteRefreshToken(eq(member.getId().toString()), anyString());
    }
}
