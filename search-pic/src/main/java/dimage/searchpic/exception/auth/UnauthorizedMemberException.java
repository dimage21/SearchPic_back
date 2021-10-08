package dimage.searchpic.exception.auth;

import dimage.searchpic.exception.CustomException;
import dimage.searchpic.exception.ErrorInfo;

public class UnauthorizedMemberException extends CustomException {
    public UnauthorizedMemberException(ErrorInfo errorInfo) {
        super(errorInfo);
    }
}