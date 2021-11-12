package dimage.searchpic.service;

import dimage.searchpic.domain.location.Location;
import dimage.searchpic.domain.location.repository.LocationRepository;
import dimage.searchpic.dto.analysis.AnalysisLocationResponse;
import dimage.searchpic.dto.location.CoordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class LocationService {
    private final LocationRepository locationRepository;
    private final RestTemplate restTemplate;
    @Value("${location.key}") private String key;
    @Value("${location.coord-url}") private String requestUrl;


    public List<AnalysisLocationResponse> findByNames(List<String> names) {
        List<Location> locations = locationRepository.findLocations(names,
                names.stream().map(Object::toString).collect(Collectors.joining(",")));
        return locations.stream().map(location->AnalysisLocationResponse.of(location,true)) // TODO: 스크립 여부 추후 수정
                .collect(Collectors.toList());
    }

    public Location requestLocationInfo(double x, double y) {
        String url = UriComponentsBuilder
                .fromHttpUrl(requestUrl)
                .queryParam("x", x)
                .queryParam("y", y)
                .build().toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization",key);

        ResponseEntity<CoordResponse> result = restTemplate.exchange(url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                CoordResponse.class,
                x,
                y
        );
        return Objects.requireNonNull(result.getBody()).createLocation(x,y);
    }

    @Transactional
    public Location findOrCreate(String repUrl, double x, double y) {
        return locationRepository.findByXAndY(x, y)
                .orElseGet(
                        () -> {
                            Location location = requestLocationInfo(x, y);
                            location.setRepImageUrl(repUrl);
                            locationRepository.save(location);
                            return location;
                        }
                );
    }
}
