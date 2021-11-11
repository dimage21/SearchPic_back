package dimage.searchpic.dto.analysis;

import dimage.searchpic.domain.location.Location;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ApiModel("장소 분석 결과 응답")
public class AnalysisLocationResponse {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("주소")
    private String address;

    @ApiModelProperty("이름")
    private String placeName;

    @ApiModelProperty("대표 이미지")
    private String repImageUrl;

    @ApiModelProperty("스크랩 여부")
    private boolean isMarked; // 이미 해당 장소 스크랩했는지 true false
    public static AnalysisLocationResponse of(Location location, Boolean isMarked) {
        return AnalysisLocationResponse.builder()
                .id(location.getId())
                .address(location.getAddress())
                .placeName(location.getPlaceName())
                .repImageUrl(location.getRepImageUrl())
                .isMarked(isMarked)
                .build();
    }
}
