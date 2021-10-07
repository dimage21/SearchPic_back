package dimage.searchpic.dto.common;
import lombok.Getter;

@Getter
public class CommonResponse<T> {
    private final boolean success;
    private final int status;
    private final String message;
    private final T data;

    public CommonResponse(boolean success, int status, String message, T data) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResponse<T> of(CommonInfo commonInfo) {
        return new CommonResponse<T>(true, commonInfo.getStatus().value(), commonInfo.getMessage(), null);
    }

    public static <T> CommonResponse<T> of(CommonInfo commonInfo, T data) {
        return new CommonResponse<T>(true, commonInfo.getStatus().value(), commonInfo.getMessage(), data);
    }
}