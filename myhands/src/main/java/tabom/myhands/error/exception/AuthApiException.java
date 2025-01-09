package tabom.myhands.error.exception;

import lombok.Getter;
import tabom.myhands.error.errorcode.ErrorCode;

@Getter
public class AuthApiException extends RuntimeException {
    private final ErrorCode errorCode;

    public AuthApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
