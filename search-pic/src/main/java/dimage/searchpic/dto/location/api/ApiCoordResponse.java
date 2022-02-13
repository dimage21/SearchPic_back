package dimage.searchpic.dto.location.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiCoordResponse extends KakaoApiResponse<ApiCoordResponse.CoordDocument> {

    @Getter
    public static class CoordDocument extends Document {
        @JsonProperty("road_address")
        RoadAddress roadAddress;
        Address address;

        @Getter
        public static class RoadAddress {
            public RoadAddress(){}
            public RoadAddress(String roadAddress) {
                this.roadAddress = roadAddress;
            }
            @JsonProperty("address_name")
            String roadAddress;
        }

        @Getter
        public static class Address {
            public Address(){}
            public Address(String address, String si, String gu, String dong) {
                this.address = address;
                this.si = si;
                this.gu = gu;
                this.dong = dong;
            }

            @JsonProperty("address_name")
            String address;
            @JsonProperty("region_1depth_name")
            String si;
            @JsonProperty("region_2depth_name")
            String gu;
            @JsonProperty("region_3depth_name")
            String dong;
        }
    }
}
