package dimage.searchpic.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;
    private final String message;

    public CustomException(ErrorInfo errorInfo) {
        this.status = errorInfo.getStatus();
        this.errorCode = errorInfo.getErrorCode();
        this.message = errorInfo.getMessage();
    }
}