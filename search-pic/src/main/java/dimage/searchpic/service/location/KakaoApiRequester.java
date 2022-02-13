package dimage.searchpic.service.location;

import dimage.searchpic.domain.location.Location;
import dimage.searchpic.dto.location.LocationQueryResponse;
import dimage.searchpic.dto.location.LocationRequest;
import dimage.searchpic.dto.location.api.ApiCoordResponse;
import dimage.searchpic.dto.location.api.ApiQueryResponse;
import dimage.searchpic.util.ResponseConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KakaoApiRequester {
    @Value("${location.key}") private String secretKey;
    @Value("${location.coord-url}") private String requestLocationUrl;
    @Value("${location.query-url}") private String requestQueryUrl;

    private final ResponseConverter responseConverter;
    private final RestTemplate restTemplate;
    private static final String HEADER_NAME = "Authorization";
    private static final String QUERY_PARAM_NAME = "query";

    private HttpHeaders kakaoApiRequestHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_NAME,secretKey);
        return headers;
    }

    public Location requestLocationInfo(double x, double y) {
        LocationRequest locationRequest = new LocationRequest(x, y);
        URI uri = UriComponentsBuilder.fromHttpUrl(requestLocationUrl)
                .queryParams(responseConverter.convert(locationRequest))
                .build()
                .toUri();

        ResponseEntity<ApiCoordResponse> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>(kakaoApiRequestHeader()),
                ApiCoordResponse.class
        );
        return getLocationInfo(x,y, Objects.requireNonNull(result.getBody()));
    }

    public List<LocationQueryResponse> requestQueryInfo(String query) {
        URI uri = UriComponentsBuilder.fromHttpUrl(requestQueryUrl)
                .queryParam(QUERY_PARAM_NAME,query)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();

        ResponseEntity<ApiQueryResponse> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>(kakaoApiRequestHeader()),
                ApiQueryResponse.class
        );
        return getUniqueTownResults(Objects.requireNonNull(result.getBody()));
    }

    private Location getLocationInfo(double x, double y, ApiCoordResponse apiCoordResponse) {
        ApiCoordResponse.CoordDocument coordDocument = apiCoordResponse.getDocuments().get(0);

        ApiCoordResponse.CoordDocument.RoadAddress roadAddress = coordDocument.getRoadAddress();
        ApiCoordResponse.CoordDocument.Address address = coordDocument.getAddress();
        return Location.builder()
                .address(roadAddress != null ?
                        roadAddress.getRoadAddress() :
                        address.getAddress())
                .gu(address.getGu())
                .si(address.getSi())
                .x(x)
                .y(y)
                .dong(address.getDong())
                .build();
    }

    // 동일한 시/구에 위치한 동네는 여러 동 중 한 동네만 반환하도록 필터하여 반환한다.
    private List<LocationQueryResponse> getUniqueTownResults(ApiQueryResponse apiQueryResponse) {
        Set<String> uniqueGuAndSi = new HashSet<>();
        return apiQueryResponse.getDocuments().stream()
                .filter(d-> uniqueGuAndSi.add(d.getAddress().getRegionSiAndGu()))
                .map(d -> new LocationQueryResponse(d.getX(), d.getY(), d.getAddressName()))
                .collect(Collectors.toList());
    }
}
