package dimage.searchpic.domain.token.repository;

import dimage.searchpic.exception.auth.ExpiredTokenException;
import dimage.searchpic.exception.auth.UnauthorizedMemberException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RedisTokenTest {
    @Autowired
    private RedisTokenRepository tokenRepository;

    private final String userId = "test";
    private final String refreshToken = "sampleToken";
    private final long validTime = 1000L * 60; // 60초

    @DisplayName("리프레시 토큰이 저장된 상태라면 유저 아이디(key)를 사용해서 리프레시 토큰 값(value)을 가지고 올 수 있다")
    @Test
    public void 리프레시_토큰값이_있을때_조회기능() {
        // given
        tokenRepository.setRefreshTokenWithExpireTime(userId,refreshToken,validTime);
        // when
        String savedToken = tokenRepository.getRefreshToken(userId);
        // then
        assertThat(savedToken).isEqualTo(refreshToken);
        assertThat(savedToken).isNotNull();
    }


    @DisplayName("리프레시 토큰의 유효시간이 지나면 유저의 리프레시 토큰을 찾을 수 없기 때문에 T003 예외가 발생한다.")
    @Test
    public void 유효기간_지나서_리프레시토큰이_없다면_예외발생() throws Exception {
        // given
        final long validTime = 1000L; // 리프레시 토큰 유효시간 1초 (샘플)
        tokenRepository.setRefreshTokenWithExpireTime(userId,refreshToken,validTime);

        // when
        Thread.sleep(1000);

        ExpiredTokenException thrownException = Assertions.assertThrows(ExpiredTokenException.class, () -> {
            tokenRepository.getRefreshToken(userId);
        });
        assertThat(thrownException.getErrorCode()).isEqualTo("T003");
    }

    @DisplayName("리프레시 토큰 정상 삭제 후 해당 유저 아이디로 리프레시 토큰을 조회하면 토큰이 없으므로 예외가 발생한다.")
    @Test
    public void 리프레시토큰_정상_삭제기능() {
        // given
        tokenRepository.setRefreshTokenWithExpireTime(userId, refreshToken, validTime);
        // when
        tokenRepository.deleteRefreshToken(userId,refreshToken);
        // then
        Assertions.assertThrows(ExpiredTokenException.class, () -> {
            tokenRepository.getRefreshToken(userId);
        });
    }

    @Test
    public void 가장_최근에_발급하지_않은_리프레시토큰으로_삭제요청시_예외발생() {
        // given
        tokenRepository.setRefreshTokenWithExpireTime(userId, refreshToken, validTime);
        final String newRefreshToken = "new";
        tokenRepository.setRefreshTokenWithExpireTime(userId, newRefreshToken, validTime);
        // when
        UnauthorizedMemberException thrownException = Assertions.assertThrows(UnauthorizedMemberException.class, () -> {
            tokenRepository.deleteRefreshToken(userId, refreshToken);
        });
        assertThat(thrownException.getErrorCode()).isEqualTo("T002");
    }

    @Test
    public void 유저가_가장_최근에_발급한_리프레시토큰인지_체크하는_기능() {
        // given
        tokenRepository.setRefreshTokenWithExpireTime(userId, refreshToken, validTime);
        final String newRefreshToken = "new";
        tokenRepository.setRefreshTokenWithExpireTime(userId, newRefreshToken, validTime);

        // when
        UnauthorizedMemberException thrownException = Assertions.assertThrows(UnauthorizedMemberException.class, () -> {
            tokenRepository.validIsRecentAndUsableToken(userId, refreshToken);
        });
        tokenRepository.validIsRecentAndUsableToken(userId, newRefreshToken);

        // then
        assertThat(thrownException.getErrorCode()).isEqualTo("T002");
    }
}
