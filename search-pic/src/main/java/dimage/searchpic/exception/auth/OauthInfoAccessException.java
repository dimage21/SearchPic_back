package dimage.searchpic.exception.auth;

import dimage.searchpic.exception.CustomException;
import dimage.searchpic.exception.ErrorInfo;

public class OauthInfoAccessException extends CustomException {
    public OauthInfoAccessException(ErrorInfo errorInfo) {
        super(errorInfo);
    }
}