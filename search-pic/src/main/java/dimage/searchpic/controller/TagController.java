package dimage.searchpic.controller;

import dimage.searchpic.dto.common.CommonInfo;
import dimage.searchpic.dto.common.CommonResponse;
import dimage.searchpic.dto.tag.TagDetailResponse;
import dimage.searchpic.service.TagService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class TagController {
    private final TagService tagService;

    @ApiOperation("인기 태그 정보를 반환")
    @ApiResponses({
            @ApiResponse(code = 200, response = TagDetailResponse.class, message = "성공")
    })
    @GetMapping("/tags")
    public ResponseEntity<?> getTopTags() {
        List<TagDetailResponse> response = tagService.getTopTags();
        return ResponseEntity.ok(CommonResponse.of(CommonInfo.SUCCESS, response));
    }
}
