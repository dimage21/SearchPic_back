package dimage.searchpic.exception.auth;

import dimage.searchpic.exception.CustomException;
import dimage.searchpic.exception.ErrorInfo;

public class ExpiredTokenException extends CustomException {
    public ExpiredTokenException(ErrorInfo errorInfo) {
        super(errorInfo);
    }
}
