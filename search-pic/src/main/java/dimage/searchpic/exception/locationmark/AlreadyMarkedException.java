package dimage.searchpic.exception.locationmark;

import dimage.searchpic.exception.CustomException;
import dimage.searchpic.exception.ErrorInfo;

public class AlreadyMarkedException extends CustomException {
    public AlreadyMarkedException(ErrorInfo errorInfo) {
        super(errorInfo);
    }
}