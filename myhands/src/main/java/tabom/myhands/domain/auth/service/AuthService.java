package tabom.myhands.domain.auth.service;


import tabom.myhands.domain.auth.dto.AuthResponse;

public interface AuthService {
    AuthResponse retoken(Long userId, boolean isAdmin, String refreshToken);
}
