package dimage.searchpic.config.auth;

import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.auth.BadTokenException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expire-time}")
    private Integer accessTokenExpireTime; // 액세스 토큰 유효시간

    @PostConstruct
    private void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // 액세스 토큰 생성
    public String createAccessToken(String pk) { // 멤버의 pk를 claims 를 포함하는 페이로드에 담아서 이를 토큰으로 발급
        Claims claims = Jwts.claims().setSubject(pk); // 담을 내용: pk
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + accessTokenExpireTime); // 토큰 만료 시각: 현재 + 유효 시간까지
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now)// 토큰 발행 시간 정보
                .setExpiration(expireTime)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 액세스 토큰에서 회원 정보(pk) 추출
    public String getPkFromAccessToken(String token) {
        return getClaimsFromJwtToken(token)
                .getSubject();
    }

    // 정보 모음(Claims) 리턴
    public Claims getClaimsFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰의 유효성 검사
    public boolean validateToken(String jwtToken) {
        try {
            getClaimsFromJwtToken(jwtToken);
            return true;
        }
        catch (ExpiredJwtException e) { // 토큰이 만료된 경우
            return false;
        }
        catch (SignatureException | MalformedJwtException | UnsupportedJwtException ex) { // 잘못된 JWT 서명이거나 지원되지 않는 JWT 토큰인 경우
            throw new BadTokenException(ErrorInfo.BAD_TOKEN);
        }
    }
}