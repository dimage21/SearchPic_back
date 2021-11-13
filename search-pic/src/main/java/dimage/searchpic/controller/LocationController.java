package dimage.searchpic.controller;

import dimage.searchpic.config.auth.CurrentMember;
import dimage.searchpic.domain.location.Location;
import dimage.searchpic.domain.member.Member;
import dimage.searchpic.dto.common.CommonInfo;
import dimage.searchpic.dto.common.CommonResponse;
import dimage.searchpic.service.LocationMarkService;
import dimage.searchpic.service.LocationService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;
    private final LocationMarkService locationMarkService;

    @ApiOperation(value = "장소 정보 확인")
    @GetMapping("/location")
    public ResponseEntity<?> checkLocation(@RequestParam double x, @RequestParam double y){
        Location location = locationService.requestLocationInfo(x, y);
        StringBuilder sb = new StringBuilder();
        sb.append(location.getAddress()).append(" (").append(location.getDong()).append(")");
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS,sb.toString()));
    }

    @ApiOperation(value = "장소 마크하기")
    @PostMapping("/location/{id}/mark")
    public ResponseEntity<?> markLocation(@ApiIgnore @CurrentMember Member member,
                                          @PathVariable("id") long locationId) {
        locationMarkService.markLocation(member.getId(),locationId);
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS));
    }

    @ApiOperation(value = "장소 마크 취소하기")
    @DeleteMapping("/location/{id}/un-mark")
    public ResponseEntity<?> unMarkLocation(@ApiIgnore @CurrentMember Member member,
                                            @PathVariable("id") long locationId) {
        locationMarkService.unMarkLocation(member.getId(),locationId);
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS));
    }
}