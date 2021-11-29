package dimage.searchpic.dto.location;

import dimage.searchpic.domain.location.Location;
import dimage.searchpic.domain.locationmark.LocationMark;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MarkLocationResponse {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("이미지 url")
    private String imageUrl;

    @ApiModelProperty("경도")
    private Double x;

    @ApiModelProperty("위도")
    private Double y;

    public static MarkLocationResponse of(LocationMark locationMark) {
        Location location = locationMark.getLocation();
        return new MarkLocationResponse(location.getId(),location.getRepImageUrl(), location.getX(), location.getY());
    }

    public static MarkLocationResponse of(Location location) {
        return new MarkLocationResponse(location.getId(),location.getRepImageUrl(), location.getX(), location.getY());
    }
}
