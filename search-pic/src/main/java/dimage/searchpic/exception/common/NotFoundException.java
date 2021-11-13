package dimage.searchpic.exception.common;

import dimage.searchpic.exception.CustomException;
import dimage.searchpic.exception.ErrorInfo;

public class NotFoundException extends CustomException {
    public NotFoundException(ErrorInfo errorInfo) {
        super(errorInfo);
    }
}
