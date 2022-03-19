package dimage.searchpic.dto.common;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel("성공 응답")
public class CommonResponse<T> {
    @ApiModelProperty(value = "성공여부", example = "true")
    private final boolean success;

    @ApiModelProperty(value = "상태코드",example = "200")
    private final int status;

    @ApiModelProperty(value = "메시지",example = "성공")
    private final String message;

    @ApiModelProperty(value = "데이터")
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
