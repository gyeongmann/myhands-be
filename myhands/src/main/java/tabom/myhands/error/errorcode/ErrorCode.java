package tabom.myhands.error.errorcode;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    int getCode();
    HttpStatus getHttpStatus();
    String getMessage();
}
