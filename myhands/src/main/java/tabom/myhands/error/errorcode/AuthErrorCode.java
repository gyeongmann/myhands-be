package tabom.myhands.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode{
    INVALID_AUTH_HEADER(2000, HttpStatus.BAD_REQUEST, "The Authorization header is missing or invalid"),
    INVALID_TOKEN(2001, HttpStatus.UNAUTHORIZED, "Invalid token"),
    EXPIRED_TOKEN(2002, HttpStatus.UNAUTHORIZED, "The token has expired"),
    REFRESH_TOKEN_MISMATCH(2003, HttpStatus.UNAUTHORIZED, "The refresh token does not match with the stored token"),
    TOKEN_BLACKLISTED(2004, HttpStatus.UNAUTHORIZED, "The token is blacklisted");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;
}
