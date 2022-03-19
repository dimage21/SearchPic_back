package dimage.searchpic.dto.location;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@ApiModel("쿼리 검색 결과 응답")
public class LocationQueryResponse {

    @ApiModelProperty("경도")
    private double x;

    @ApiModelProperty("위도")
    private double y;

    @ApiModelProperty("이름")
    private String placeName;
}
