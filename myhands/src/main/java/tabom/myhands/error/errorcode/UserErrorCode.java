package tabom.myhands.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode{

    ID_ALREADY_EXISTS(1000, HttpStatus.BAD_REQUEST, "ID is already in use"),
    INVALID_DEPARTMENT_VALUE(1001, HttpStatus.BAD_REQUEST, "Invalid Department value"),
    EMPLOYEE_NUM_ALREADY_EXISTS(1002, HttpStatus.BAD_REQUEST, "Employee number is already in use"),
    USER_ID_NOT_FOUND(1003, HttpStatus.NOT_FOUND, "User not found with the given ID"),
    LOGIN_FAILED(1004, HttpStatus.UNAUTHORIZED, "Invalid email or password."),
    INVALID_LEVEL_VALUE(1005, HttpStatus.BAD_REQUEST, "Invalid Level value"),
    PASSWORD_CANNOT_BE_EMPTY(1006, HttpStatus.BAD_REQUEST, "Password cannot be empty"),
    AVARTAID_CANNOT_BE_EMPTY(1007, HttpStatus.BAD_REQUEST, "AvartaId cannot be empty"),
    NAME_CANNOT_BE_EMPTY(1008, HttpStatus.BAD_REQUEST, "Name cannot be empty"),
    EMPLOYEE_NUM_CANNOT_BE_EMPTY(1009, HttpStatus.BAD_REQUEST, "Employee number cannot be empty"),
    DEPARTMENT_CANNOT_BE_EMPTY(1010, HttpStatus.BAD_REQUEST, "Department cannot be empty"),
    JOB_GROUP_CANNOT_BE_EMPTY(1011, HttpStatus.BAD_REQUEST, "Job group cannot be empty"),
    JOINED_AT_CANNOT_BE_EMPTY(1012, HttpStatus.BAD_REQUEST, "Joined date cannot be empty");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;
}
