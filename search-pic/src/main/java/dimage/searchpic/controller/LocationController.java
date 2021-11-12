package dimage.searchpic.controller;

import dimage.searchpic.domain.location.Location;
import dimage.searchpic.dto.common.CommonInfo;
import dimage.searchpic.dto.common.CommonResponse;
import dimage.searchpic.service.LocationService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @ApiOperation(value = "장소 정보 확인")
    @GetMapping("/location")
    public ResponseEntity<?> checkLocation(@RequestParam double x, @RequestParam double y){
        Location location = locationService.requestLocationInfo(x, y);
        StringBuilder sb = new StringBuilder();
        sb.append(location.getAddress()).append(" (").append(location.getDong()).append(")");
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS,sb.toString()));
    }
}