package dimage.searchpic.dto.location;

import dimage.searchpic.domain.location.Location;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ApiModel("장소 분석 결과 응답")
public class LocationResponse {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("주소")
    private String address;

    @ApiModelProperty("경도")
    private Double x;

    @ApiModelProperty("위도")
    private Double y;

    @ApiModelProperty("이름")
    private String placeName;

    @ApiModelProperty("대표 이미지")
    private String repImageUrl;

    @ApiModelProperty("대표 태그")
    private List<String> repTags;

    @ApiModelProperty("스크랩 여부")
    private boolean isMarked; // 이미 해당 장소 스크랩했는지 true false

    public static LocationResponse of(Location location, Boolean isMarked,List<String> repTags) {
        return LocationResponse.builder()
                .id(location.getId())
                .address(location.getAddress())
                .x(location.getX())
                .y(location.getY())
                .placeName(location.getPlaceName())
                .repImageUrl(location.getRepImageUrl())
                .isMarked(isMarked)
                .repTags(repTags)
                .build();
    }
}
