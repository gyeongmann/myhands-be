package tabom.myhands.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tabom.myhands.common.response.ErrorResponse;
import tabom.myhands.common.response.MessageResponse;
import tabom.myhands.error.exception.AuthApiException;
import tabom.myhands.error.exception.BoardApiException;
import tabom.myhands.error.exception.UserApiException;

@RestControllerAdvice
@Slf4j
public class BaseControllerAdvice {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<MessageResponse> dataIntegrityViolationException(Exception e, HttpServletRequest req) {
        log.debug("Data Integrity Violation");
        log.error(req.getRequestURI());
        log.error(e.getClass().getCanonicalName());
        e.printStackTrace();
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Data Integrity Violation"));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<MessageResponse> missingServletRequestParameterException(Exception e, HttpServletRequest req) {
        log.debug("Missing Parameter");
        log.error(req.getRequestURI());
        log.error(e.getClass().getCanonicalName());
        e.printStackTrace();
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(MessageResponse.of(HttpStatus.BAD_REQUEST, "Missing Parameter"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MessageResponse> httpMessageNotReadableException(Exception e, HttpServletRequest req) {
        log.debug("HttpMessageNotReadableException");
        log.error(req.getRequestURI());
        log.error(e.getClass().getCanonicalName());
        e.printStackTrace();
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(MessageResponse.of(HttpStatus.BAD_REQUEST, "Bad Request"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> unknownException(Exception e, HttpServletRequest req) {
        log.debug("UNKNOWN ERROR");
        log.error(req.getRequestURI());
        log.error(e.getClass().getCanonicalName());
        e.printStackTrace();
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown Error"));
    }

    @ExceptionHandler(UserApiException.class)
    public ResponseEntity<ErrorResponse> handleUserApiException(UserApiException e, HttpServletRequest req) {
        log.debug("UserApiException");
        log.error(req.getRequestURI());
        log.error(e.getClass().getCanonicalName());
        log.error(e.getErrorCode().getMessage());

        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(ErrorResponse.of(e.getErrorCode()));
    }

    @ExceptionHandler(AuthApiException.class)
    public ResponseEntity<ErrorResponse> handleAuthApiException(AuthApiException e, HttpServletRequest req) {
        log.debug("AuthApiException");
        log.error(req.getRequestURI());
        log.error(e.getClass().getCanonicalName());
        log.error(e.getErrorCode().getMessage());

        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(ErrorResponse.of(e.getErrorCode()));
    }

    @ExceptionHandler(BoardApiException.class)
    public ResponseEntity<ErrorResponse> handleBoardApiException(BoardApiException e, HttpServletRequest req) {
        log.debug("BoardApiException");
        log.error(req.getRequestURI());
        log.error(e.getClass().getCanonicalName());
        log.error(e.getErrorCode().getMessage());

        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(ErrorResponse.of(e.getErrorCode()));
    }
}