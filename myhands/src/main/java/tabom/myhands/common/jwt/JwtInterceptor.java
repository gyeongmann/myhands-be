package tabom.myhands.common.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tabom.myhands.common.infra.redis.RedisService;
import tabom.myhands.domain.user.repository.UserRepository;
import tabom.myhands.error.errorcode.AuthErrorCode;
import tabom.myhands.error.errorcode.UserErrorCode;
import tabom.myhands.error.exception.AuthApiException;
import tabom.myhands.error.exception.UserApiException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisService redisService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader("Authorization");
        log.info(getIp(request));

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AuthApiException(AuthErrorCode.INVALID_AUTH_HEADER);
        }

        String jwt = authorizationHeader.substring(7);
        try {
            Long userId = jwtTokenProvider.getUserIdFromToken(jwt);
            boolean isAdmin = jwtTokenProvider.getIsAdminFromToken(jwt);

            if (!jwtTokenProvider.validateToken(jwt, userId, isAdmin)) {
                throw new AuthApiException(AuthErrorCode.INVALID_TOKEN);
            }

            if (redisService.isBlacklisted(jwt)) {
                throw new AuthApiException(AuthErrorCode.TOKEN_BLACKLISTED);
            }

            if (!userRepository.existsById(userId)) {
                throw new UserApiException(UserErrorCode.USER_ID_NOT_FOUND);
            }

            request.setAttribute("userId", userId);
            request.setAttribute("isAdmin", isAdmin);
            return true;

        } catch (AuthApiException e) {
            log.error("JWT Authentication failed: {}", e.getMessage());
            throw e;
        }
    }

    private String getIp(HttpServletRequest request) {
        String ret = null;

        ret = request.getHeader("X-Forwarded-For");
        if (ret== null) {
            ret = request.getHeader("Proxy-Client-IP");
        }
        if (ret == null) {
            ret = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ret == null) {
            ret = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ret == null) {
            ret = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ret == null) {
            ret = request.getRemoteAddr();
        }
        if (ret != null && ret.contains(",")) {
            ret = ret.split(",")[0].trim();
        }

        return ret;
    }
}
