package tabom.myhands.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tabom.myhands.common.infra.redis.RedisService;
import tabom.myhands.common.jwt.JwtTokenProvider;
import tabom.myhands.domain.auth.dto.AuthResponse;
import tabom.myhands.error.errorcode.AuthErrorCode;
import tabom.myhands.error.exception.AuthApiException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Override
    public AuthResponse retoken(Long userId, boolean isAdmin, String refreshToken) {
        String redisKey = isAdmin ? "admin:" + userId : "user:" + userId;
        String storedToken = redisService.getStringValue(redisKey);

        if (storedToken == null) {
            throw new AuthApiException(AuthErrorCode.INVALID_TOKEN);
        }

        if (!storedToken.equals(refreshToken)) {
            throw new AuthApiException(AuthErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, isAdmin);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId, isAdmin);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}
