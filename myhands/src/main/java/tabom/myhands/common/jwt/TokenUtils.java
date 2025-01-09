package tabom.myhands.common.jwt;

import tabom.myhands.error.errorcode.AuthErrorCode;
import tabom.myhands.error.exception.AuthApiException;

public class TokenUtils {
    private static final String BEARER_PREFIX = "Bearer ";

    public static String extractToken(String header) {
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        throw new AuthApiException(AuthErrorCode.INVALID_AUTH_HEADER);
    }
}
