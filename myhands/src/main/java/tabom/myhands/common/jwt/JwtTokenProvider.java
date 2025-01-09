package tabom.myhands.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tabom.myhands.common.infra.redis.RedisService;
import tabom.myhands.common.properties.JwtProperties;
import tabom.myhands.error.errorcode.AuthErrorCode;
import tabom.myhands.error.exception.AuthApiException;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final RedisService redisService;
    private Key key;

    @PostConstruct
    protected void init() {
        String secret = jwtProperties.getSecretKey();
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Long userId, boolean isAdmin) {
        return generateToken(userId, isAdmin, jwtProperties.getAccessTokenExpireTime());
    }

    public String generateRefreshToken(Long userId, boolean isAdmin) {
        String refreshToken = generateToken(userId, isAdmin, jwtProperties.getRefreshTokenExpireTime());

        String redisKey = isAdmin ? "admin:" + userId : "user:" + userId; // Redis 키 생성: 어드민 여부를 포함

        redisService.setStringValueAndExpire(redisKey, refreshToken, jwtProperties.getRefreshTokenExpireTime());
        return refreshToken;
    }

    public String generateToken(Long userId, boolean isAdmin, long expirationTime) {
        Claims claims = Jwts.claims().setSubject(userId.toString());
        claims.put("userId", userId.toString());
        claims.put("isAdmin", isAdmin);
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireTime)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token, Long userId, boolean isAdmin) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (claims.getExpiration().before(new Date())) {
                throw new AuthApiException(AuthErrorCode.EXPIRED_TOKEN);
            }

            String redisKey = isAdmin ? "admin:" + userId : "user:" + userId;
            String storedToken = redisService.getStringValue(redisKey);

            if (storedToken == null || !storedToken.equals(token)) {
                throw new AuthApiException(AuthErrorCode.INVALID_TOKEN);
            }

            return true;
        } catch (ExpiredJwtException e) {
            throw new AuthApiException(AuthErrorCode.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new AuthApiException(AuthErrorCode.INVALID_TOKEN);
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Long.valueOf(claims.get("userId", String.class));
        } catch (Exception e) {
            throw new AuthApiException(AuthErrorCode.INVALID_TOKEN);
        }
    }

    public boolean getIsAdminFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("isAdmin", Boolean.class);
        } catch (Exception e) {
            throw new AuthApiException(AuthErrorCode.INVALID_TOKEN);
        }
    }

    // 엑세스 토큰 무효화하기 위한 만료 시간 계산
    public long getExpirationTime(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }
}

