package tabom.myhands.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tabom.myhands.common.infra.redis.RedisService;
import tabom.myhands.common.jwt.TokenUtils;
import tabom.myhands.common.properties.ResponseProperties;
import tabom.myhands.common.response.DtoResponse;
import tabom.myhands.domain.auth.dto.AuthResponse;
import tabom.myhands.domain.auth.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final ResponseProperties responseProperties;
    private final RedisService redisService;

    @PostMapping("/retoken")
    public ResponseEntity<DtoResponse<AuthResponse>> retoken(HttpServletRequest request, @RequestHeader("Authorization") String refreshTokenHeader) {
        Long userId = (Long) request.getAttribute("userId");
        boolean isAdmin = (boolean) request.getAttribute("isAdmin");
        String refreshToken = TokenUtils.extractToken(refreshTokenHeader);
        AuthResponse response = authService.retoken(userId, isAdmin, refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(DtoResponse.of(HttpStatus.OK, responseProperties.getSuccess(), response));
    }
}
