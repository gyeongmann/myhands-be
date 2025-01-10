package tabom.myhands.error.exception;

import lombok.Getter;
import tabom.myhands.error.errorcode.ErrorCode;

@Getter
public class BoardApiException extends RuntimeException {
    private final ErrorCode errorCode;

    public BoardApiException(ErrorCode errorCode) {
      super(errorCode.getMessage());
      this.errorCode = errorCode;
    }
}

