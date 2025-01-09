package tabom.myhands.error.exception;

import tabom.myhands.error.errorcode.ErrorCode;
import lombok.Getter;

@Getter
public class UserApiException extends RuntimeException {
  private final ErrorCode errorCode;

  public UserApiException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
