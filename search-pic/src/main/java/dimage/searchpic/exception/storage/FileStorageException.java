package dimage.searchpic.exception.storage;

import dimage.searchpic.exception.CustomException;
import dimage.searchpic.exception.ErrorInfo;

public class FileStorageException extends CustomException {
    public FileStorageException(ErrorInfo errorInfo) {
        super(errorInfo);
    }
}
