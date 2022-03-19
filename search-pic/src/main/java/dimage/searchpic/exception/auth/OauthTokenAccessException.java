package dimage.searchpic.exception.auth;

import dimage.searchpic.exception.CustomException;
import dimage.searchpic.exception.ErrorInfo;

public class OauthTokenAccessException extends CustomException {
    public OauthTokenAccessException(ErrorInfo errorInfo) {
        super(errorInfo);
    }
}
