package dimage.searchpic.exception.auth;

import dimage.searchpic.exception.CustomException;
import dimage.searchpic.exception.ErrorInfo;

public class BadTokenException extends CustomException {
    public BadTokenException(ErrorInfo errorInfo) {
        super(errorInfo);
    }
}
