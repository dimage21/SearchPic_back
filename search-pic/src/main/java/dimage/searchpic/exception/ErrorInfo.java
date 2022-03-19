package dimage.searchpic.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorInfo {
    BAD_TOKEN(BAD_REQUEST,"T001","잘못된 토큰입니다."),
    NOT_AUTHORIZED_USER(UNAUTHORIZED,"T002","유효한 토큰이 필요합니다."),
    EXPIRED_TOKEN(UNAUTHORIZED,"T003","만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(BAD_REQUEST,"T004","지원하지 않는 토큰입니다"),

    MAX_TAG_COUNT_LIMIT(BAD_REQUEST,"D001","태그는 최대 5개까지 가능합니다."),
    ALREADY_MARKED_PLACE(BAD_REQUEST,"D002","이미 추가된 장소입니다."),
    NOT_MARKED_PLACE(BAD_REQUEST,"D003","추가하지 않은 장소입니다."),
    MAX_TAG_LENGTH_LIMIT(BAD_REQUEST,"D004","태그는 최대 15자까지 허용됩니다"),

    OAUTH_GET_INFO_FAIL(UNAUTHORIZED,"S001","소셜사 토큰으로부터 유저 정보를 받아오는데 실패했습니다."),
    FILE_UPLOAD_ERROR(INTERNAL_SERVER_ERROR,"S004","파일 업로드 과정에서 에러가 발생했습니다."),
    MEMBER_NULL(NOT_FOUND, "S005", "존재하지 않는 사용자입니다."),
    DUPLICATE_NICKNAME(CONFLICT,"S006","이미 존재하는 닉네임입니다."),
    UNSUPPORTED_OAUTH(BAD_REQUEST,"S007","지원하지 않는 소셜 네트워크 서비스사입니다."),
    ANALYSIS_FAIL(INTERNAL_SERVER_ERROR,"S008","분석 서버에서 결과를 받아오는데 실패했습니다."),

    NOT_ALLOWED_ACCESS(FORBIDDEN,"S009","접근 권한이 없습니다."),
    LOCATION_NULL(NOT_FOUND, "S010", "존재하지 않는 장소입니다."),
    POST_NULL(NOT_FOUND, "S010", "존재하지 않는 글입니다.");
    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
