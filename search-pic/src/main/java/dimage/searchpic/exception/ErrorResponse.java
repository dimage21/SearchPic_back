package dimage.searchpic.exception;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ApiModel("실패 응답")
public class ErrorResponse {
    @ApiModelProperty(value = "성공여부",example = "false")
    private final boolean success;
    @ApiModelProperty(value = "상태코드",example = "400")
    private final int status;
    @ApiModelProperty(value = "에러코드",example = "에러코드")
    private final String errorCode;
    @ApiModelProperty("메시지")
    private final String message;

    public static ErrorResponse of(CustomException ex){
        return new ErrorResponse(false, ex.getStatus().value(),ex.getErrorCode(),ex.getMessage());
    }
}
