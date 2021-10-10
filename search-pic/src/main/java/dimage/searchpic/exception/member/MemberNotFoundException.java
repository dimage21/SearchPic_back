package dimage.searchpic.exception.member;

import dimage.searchpic.exception.CustomException;
import dimage.searchpic.exception.ErrorInfo;

public class MemberNotFoundException extends CustomException {
    public MemberNotFoundException(ErrorInfo errorInfo) {
        super(errorInfo);
    }
}
