package dimage.searchpic.exception.member;

import dimage.searchpic.exception.CustomException;
import dimage.searchpic.exception.ErrorInfo;

public class NicknameDuplicateException extends CustomException {
    public NicknameDuplicateException(ErrorInfo errorInfo) {
        super(errorInfo);
    }
}
