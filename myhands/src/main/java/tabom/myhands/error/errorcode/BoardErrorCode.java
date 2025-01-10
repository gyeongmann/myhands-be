package tabom.myhands.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardErrorCode implements ErrorCode{

    INVALID_SIZE_PARAMETER(3000, HttpStatus.BAD_REQUEST, "The size parameter must be greater than 0"),
    BOARD_ID_NOT_FOUND(3001, HttpStatus.NOT_FOUND, "Board not found with the given ID");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

}
