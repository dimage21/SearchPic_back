package dimage.searchpic.config.auth;

import dimage.searchpic.dto.auth.TokenResponse;
import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.auth.BadTokenException;
import dimage.searchpic.exception.auth.UnauthorizedMemberException;
import dimage.searchpic.service.auth.TokenInfo;
import dimage.searchpic.service.auth.TokenInfoProvider;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final TokenInfoProvider tokenKeyProvider;

    // 액세스 토큰과 리프레시 토큰 생성
    public TokenResponse createAccessAndRefreshTokens(String pk) {
        String accessToken = createToken(pk, tokenKeyProvider.getAccessTokenInfo().getValidTime(), tokenKeyProvider.getAccessTokenInfo().getSecretKey());
        String refreshToken = createToken(pk, tokenKeyProvider.getRefreshTokenInfo().getValidTime(), tokenKeyProvider.getRefreshTokenInfo().getSecretKey());
        return TokenResponse.of(accessToken, refreshToken);
    }

    private String createToken(String pk, long validTime, String secretKey) {
        Claims claims = Jwts.claims().setSubject(pk); // 담을 내용: pk
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + validTime); // 토큰 만료 시각: 현재 + 유효 시간까지
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now)// 토큰 발행 시간 정보
                .setExpiration(expireTime)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 액세스 및 리프레시 토큰에서 회원 정보(pk) 추출
    public String getPkFromToken(String token,String type) {
        return getClaimsFromJwtToken(token,type)
                .getSubject();
    }

    // 파라미터 type("access" or "refresh")에 해당하는 토큰에 저장된 정보 모음을(Claims) 리턴
    public Claims getClaimsFromJwtToken(String token,String type) {
        TokenInfo tokenInfo = tokenKeyProvider.getSameTypeTokenInfo(type);
        return Jwts.parser()
                .setSigningKey(tokenInfo.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰의 유효성 검사: 유효하면 true, 만료되었으면 false 를 리턴하고
    // 이 외의 경우(잘못된 토큰 or 지원되지 않는 토큰 or 토큰이 없는 경우) Custom Exception 예외 발생시킴
    public boolean isValidToken(String jwtToken,String type) {
        try {
            getClaimsFromJwtToken(jwtToken,type);
            return true;
        } catch (ExpiredJwtException e) { // 토큰이 만료된 경우
            return false;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException ex) { // 잘못된 JWT 서명이거나 지원되지 않는 JWT 토큰인 경우
            throw new BadTokenException(ErrorInfo.BAD_TOKEN);
        } catch (IllegalArgumentException ex) {
            throw new UnauthorizedMemberException(ErrorInfo.NOT_AUTHORIZED_USER); // 토큰 없는 경우
        }
    }
}