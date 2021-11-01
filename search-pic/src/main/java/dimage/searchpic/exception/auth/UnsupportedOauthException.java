package dimage.searchpic.exception.auth;

import dimage.searchpic.exception.CustomException;
import dimage.searchpic.exception.ErrorInfo;

public class UnsupportedOauthException extends CustomException {
    public UnsupportedOauthException(ErrorInfo errorInfo) {
        super(errorInfo);
    }
}