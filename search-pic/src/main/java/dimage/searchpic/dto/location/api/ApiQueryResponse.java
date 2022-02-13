package dimage.searchpic.dto.location.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ApiQueryResponse extends KakaoApiResponse<ApiQueryResponse.QueryDocument> {

    @Getter
    public static class QueryDocument extends Document {
        Address address;

        @JsonProperty("address_name")
        String addressName;

        @JsonProperty("x")
        double x;
        @JsonProperty("y")
        double y;

        @Getter
        public static class Address {
            public Address(){}
            @JsonProperty("region_2depth_name")
            String regionSiAndGu;
        }
    }
}
