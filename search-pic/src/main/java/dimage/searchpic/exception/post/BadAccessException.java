package dimage.searchpic.exception.post;

import dimage.searchpic.exception.CustomException;
import dimage.searchpic.exception.ErrorInfo;

public class BadAccessException extends CustomException {
    public BadAccessException(ErrorInfo errorInfo) {
        super(errorInfo);
    }
}
