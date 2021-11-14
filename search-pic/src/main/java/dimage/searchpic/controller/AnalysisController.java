package dimage.searchpic.controller;

import dimage.searchpic.config.auth.CurrentMember;
import dimage.searchpic.domain.member.Member;
import dimage.searchpic.dto.location.LocationResponse;
import dimage.searchpic.dto.analysis.AnalysisResponse;
import dimage.searchpic.dto.common.CommonInfo;
import dimage.searchpic.dto.common.CommonResponse;
import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.location.AnalysisFailException;
import dimage.searchpic.service.LocationService;
import dimage.searchpic.util.ResponseConverter;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AnalysisController {
    public final RestTemplate restTemplate;
    public final ResponseConverter responseConverter;
    public final LocationService locationService;
    @Value("${ml.server}")
    private String serverUrl;

    @ApiOperation(value = "장소 분석")
    @ApiResponses({
            @ApiResponse(code = 200, message = "TOP3 장소를 반환한다", response = LocationResponse.class)
    })
    @PostMapping(value = "/analysis")
    public ResponseEntity<?> searchPlace(@RequestParam("image")  MultipartFile multipartFile,
                                         @ApiIgnore @CurrentMember Member member) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image",multipartFile.getResource());
        ResponseEntity<AnalysisResponse> result;

        try {
            result = restTemplate.postForEntity(
                    serverUrl + "/predict",
                    new HttpEntity<>(body, headers),
                    AnalysisResponse.class);
        } catch (Exception e){
            throw new AnalysisFailException(ErrorInfo.ANALYSIS_FAIL);
        }

        AnalysisResponse response = result.getBody();
        ArrayList<String> topList = response.getData();
        List<LocationResponse> results = locationService.findByNames(topList,member.getId());
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS,results));
    }
}