package dimage.searchpic.exception.post;

import dimage.searchpic.exception.CustomException;
import dimage.searchpic.exception.ErrorInfo;

public class MaxTagSizeException extends CustomException {
    public MaxTagSizeException(ErrorInfo errorInfo) {
        super(errorInfo);
    }
}