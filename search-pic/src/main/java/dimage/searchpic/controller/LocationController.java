package dimage.searchpic.controller;

import dimage.searchpic.config.auth.CurrentMember;
import dimage.searchpic.domain.location.Location;
import dimage.searchpic.domain.member.Member;
import dimage.searchpic.dto.location.LocationQueryResponse;
import dimage.searchpic.dto.location.LocationResponse;
import dimage.searchpic.dto.common.CommonInfo;
import dimage.searchpic.dto.common.CommonResponse;
import dimage.searchpic.dto.location.MarkLocationResponse;
import dimage.searchpic.service.LocationMarkService;
import dimage.searchpic.service.LocationService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;
    private final LocationMarkService locationMarkService;

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

    @ApiOperation("하나의 장소 관련 정보 조회")
    @GetMapping("/location/{id}")
    public ResponseEntity<?> getLocation(@ApiIgnore @CurrentMember Member member,
                                         @PathVariable("id") long locationId) {
        LocationResponse response = locationService.findOnePlace(locationId, member.getId());
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS, response));
    }

    @ApiOperation("모든 마커 조회")
    @GetMapping("/locations/filter")
    public ResponseEntity<?> getLocations() {
        List<MarkLocationResponse> response = locationService.findAllLocations();
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS,response));
    }

    @ApiOperation("멤버가 등록한 장소 관련 마커 조회")
    @GetMapping("/locations/filter/member")
    public ResponseEntity<?> myUploadLocations(@ApiIgnore @CurrentMember Member member){
        List<MarkLocationResponse> response = locationService.findLocationsMemberWrite(member.getId());
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS,response));
    }

    @ApiOperation("멤버가 마크한 장소 관련 마커 조회")
    @GetMapping("/locations/filter/mark")
    public ResponseEntity<?> myLikeLocations(@ApiIgnore @CurrentMember Member member){
        List<MarkLocationResponse> response = locationService.findLocationsMemberMarked(member.getId());
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS,response));
    }

    @ApiOperation("현재 위치 근처에 존재하는 장소 정보 조회")
    @GetMapping(value = {"/locations/{distance}", "/locations"})
    public ResponseEntity<?> nearSpots(@ApiIgnore @CurrentMember Member member,
                                       @RequestParam double x, @RequestParam double y,
                                       @PathVariable(value = "distance",required = false) Optional<Double> distanceKm){
        double distance = distanceKm.orElse(1.0);
        List<LocationResponse> response = locationService.nearSpotsInfo(member.getId(),x, y,distance);
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS,response));
    }

    // 카카오에 api 요청
    @ApiOperation(value = "장소 위치 정보 확인")
    @GetMapping("/api/location")
    public ResponseEntity<?> checkLocation(@RequestParam double x, @RequestParam double y){
        Location location = locationService.requestLocationInfo(x, y);
        StringBuilder sb = new StringBuilder();
        sb.append(location.getAddress()).append(" (").append(location.getDong()).append(")");
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS,sb.toString()));
    }

    @ApiOperation(value = "동네 이름으로 검색하여 가능한 동네 이름들과 위치 정보 확인")
    @GetMapping("/api/locations")
    public ResponseEntity<?> getPossibleDongsInfo(@RequestParam String query) {
        List<LocationQueryResponse> response = locationService.requestQueryInfo(query);
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS, response));
    }
}
