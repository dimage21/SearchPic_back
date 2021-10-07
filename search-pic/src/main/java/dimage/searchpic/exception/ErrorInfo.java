package dimage.searchpic.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorInfo {
    BAD_TOKEN(BAD_REQUEST,"A001","잘못된 토큰입니다."),

    MAX_TAG_SIZE_LIMIT(BAD_REQUEST,"D001","태그는 최대 5개까지 가능합니다."),
    ALREADY_MARKED_PLACE(BAD_REQUEST,"D002","이미 추가된 장소입니다."),

    OAUTH_GET_INFO_FAIL(UNAUTHORIZED,"S001","토큰으로부터 유저 정보를 받아오는데 실패했습니다."),
    OAUTH_GET_TOKEN_FAIL(UNAUTHORIZED,"S002","토큰을 받아오는데 실패했습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}