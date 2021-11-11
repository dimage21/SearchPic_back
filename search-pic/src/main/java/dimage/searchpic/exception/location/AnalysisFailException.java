package dimage.searchpic.exception.location;

import dimage.searchpic.exception.CustomException;
import dimage.searchpic.exception.ErrorInfo;

public class AnalysisFailException extends CustomException {
    public AnalysisFailException(ErrorInfo errorInfo) {
        super(errorInfo);
    }
}
