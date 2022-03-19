package dimage.searchpic.domain.token.repository;

import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.auth.ExpiredTokenException;
import dimage.searchpic.exception.auth.UnauthorizedMemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisTokenRepository {
    private final RedisTemplate<String, String> tokenRedisTemplate;

    // userId로 저장된 리프레시 토큰을 반환하며, 만약 저장된 리프레시 토큰이 없을 경우 유효기간 만료 예외를 발생시킨다.
    public String getRefreshToken(String userId) {
        ValueOperations<String, String> values = tokenRedisTemplate.opsForValue();
        if (values.get(userId) == null)
            throw new ExpiredTokenException(ErrorInfo.EXPIRED_TOKEN);
        return values.get(userId);
    }

    // 해당 유저의 이름을 키로, 리프레시 토큰을 값으로, 지속시간은 리프레시 토큰 유효시간만큼 설정하여 저장한다.
    public void setRefreshTokenWithExpireTime(String userId, String refreshToken, long validTime) {
        ValueOperations<String, String> values = tokenRedisTemplate.opsForValue();
        values.set(userId, refreshToken, Duration.ofMillis(validTime));
    }

    /*
        전달받은 리프레시 토큰이 가장 최근에 발급한 리프레시 토큰이 맞다면, 저장한 토큰을 삭제한다.
        가장 최근에 발급한 토큰이 아니라면 유효한 토큰이 필요하다는 예외를 발생시킨다.
     */
    public void deleteRefreshToken(String userId,String refreshToken) {
        if (!getRefreshToken(userId).equals(refreshToken)) {
            throw new UnauthorizedMemberException(ErrorInfo.NOT_AUTHORIZED_USER);
        }
        tokenRedisTemplate.delete(userId);
    }

    /*
        전달받은 리프레시 토큰이 가장 최근에 발급한 토큰이 아니라면 유효한 토큰이 필요하다는 예외가 발생하고,
        토큰이 만료되었으면 getRefreshToken 메서드 내부에서 유효기간 만료 예외가 발생한다.
     */
    public void validIsRecentAndUsableToken(String userId, String refreshToken) {
        if (!getRefreshToken(userId).equals(refreshToken))
            throw new UnauthorizedMemberException(ErrorInfo.NOT_AUTHORIZED_USER);
    }
}
