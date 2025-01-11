package tabom.myhands.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardErrorCode implements ErrorCode{

    INVALID_SIZE_PARAMETER(3000, HttpStatus.BAD_REQUEST, "The size parameter must be greater than 0"),
    BOARD_ID_NOT_FOUND(3001, HttpStatus.NOT_FOUND, "Board not found with the given ID"),
    NOT_ADMIN(3002, HttpStatus.FORBIDDEN, "Access denied: Only admins can access this resource."),
    TITLE_OR_CONTENT_MISSING(3003, HttpStatus.BAD_REQUEST, "Title and content must not be empty");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

}
