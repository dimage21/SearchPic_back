package dimage.searchpic.dto.location;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LocationRequest {
    private double x;
    private double y;

    public LocationRequest() {
    }
}