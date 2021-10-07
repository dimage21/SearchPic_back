package dimage.searchpic.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> commonExceptionHandler(CustomException exception) {
        ErrorResponse response = ErrorResponse.of(exception);
        return ResponseEntity.status(exception.getStatus()).body(response);
    }
}