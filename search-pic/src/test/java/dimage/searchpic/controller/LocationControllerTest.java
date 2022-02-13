package dimage.searchpic.controller;


import dimage.searchpic.config.auth.JwtTokenProvider;
import dimage.searchpic.domain.location.Location;
import dimage.searchpic.dto.location.LocationQueryResponse;
import dimage.searchpic.service.LocationMarkService;
import dimage.searchpic.service.LocationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocationController.class)
public class LocationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    @MockBean
    private LocationMarkService locationMarkService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("x,y(위 경도)로 조회했을 경우 해당 위치의 장소 관련 정보를 반환한다.")
    @Test
    public void requestInfoByCoordinate() throws Exception {
        // given
        double x = 127.12429884602258;
        double y = 37.3928856662915;
        Location sampleLocation = Location.builder()
                .address("경기 성남시 분당구 이매동 130")
                .dong("이매동")
                .build();
        given(locationService.requestLocationInfo(x, y)).willReturn(sampleLocation);
        // when
        ResultActions response = mockMvc.perform(get("http://localhost:8080/api/location")
                .param("x", Double.toString(x))
                .param("y", Double.toString(y)));
        // then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(sampleLocation.getAddress()+" ("+sampleLocation.getDong()+")")));
    }

    @DisplayName("쿼리(ex. 동 이름)으로 조회했을 경우 가능한 동네 관련 정보를 반환한다.")
    @Test
    public void requestInfoByQuery() throws Exception {
        // given
        String queryName = "평촌동";
        List<LocationQueryResponse> sampleResponse = List.of(
                new LocationQueryResponse(126.977084010595, 37.3946500892517, "경기 안양시 동안구 평촌동"));
        given(locationService.requestQueryInfo(queryName)).willReturn(sampleResponse);
        // when
        ResultActions response = mockMvc.perform(get("http://localhost:8080/api/locations")
                .param("query", queryName));
        // then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].x",is(sampleResponse.get(0).getX())))
                .andExpect(jsonPath("$.data[0].y",is(sampleResponse.get(0).getY())))
                .andExpect(jsonPath("$.data[0].placeName",is(sampleResponse.get(0).getPlaceName())));
    }
}