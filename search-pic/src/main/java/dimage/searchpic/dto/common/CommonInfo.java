package dimage.searchpic.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.OK;

@Getter
@AllArgsConstructor
public enum CommonInfo {
    SUCCESS(OK,"성공"),
    LOGIN_SUCCESS(OK, "로그인 성공");

    private final HttpStatus status;
    private final String message;
}
