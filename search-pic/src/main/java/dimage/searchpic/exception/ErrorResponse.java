package dimage.searchpic.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final boolean success;
    private final int status;
    private final String errorCode;
    private final String message;

    public static ErrorResponse of(CustomException ex){
        return new ErrorResponse(false, ex.getStatus().value(),ex.getErrorCode(),ex.getErrorCode());
    }
}