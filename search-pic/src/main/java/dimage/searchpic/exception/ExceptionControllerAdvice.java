package dimage.searchpic.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> commonExceptionHandler(CustomException exception) {
        ErrorResponse response = ErrorResponse.of(exception);
        return ResponseEntity.status(exception.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // validation 에러 발생할 경우
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errorMessageList = new ArrayList<>();
        ex.getBindingResult().getAllErrors()
            .forEach( error -> {
                // 에러가 발생한 필드 이름과 에러 메시지를 문자열에 담음
                String errorMessage = String.format("[%s : %s]", ((FieldError) error).getField(), error.getDefaultMessage());
                errorMessageList.add(errorMessage);
                }
            );
        String errorMessages = String.join(", ", errorMessageList);
        ErrorResponse errorResponse = new ErrorResponse(false, HttpStatus.BAD_REQUEST.value(), "Validation", errorMessages);
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }
}