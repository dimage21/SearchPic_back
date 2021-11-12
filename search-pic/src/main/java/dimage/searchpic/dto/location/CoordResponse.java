package dimage.searchpic.dto.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import dimage.searchpic.domain.location.Location;
import lombok.Getter;
import java.util.List;

@Getter
public class CoordResponse {
    private CoordResponse(){}
    public CoordResponse(List<Document> documents) {
        this.documents = documents;
    }

    private List<Document> documents;

    @Getter
    private static class Document {
        @JsonProperty("road_address")
        RoadAddress roadAddress;
        Address address;

        @Getter
        private static class RoadAddress {
            public RoadAddress(){}
            public RoadAddress(String roadAddress) {
                this.roadAddress = roadAddress;
            }
            @JsonProperty("address_name")
            String roadAddress;
        }

        @Getter
        private static class Address {
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

    public Location createLocation(double x,double y) {
        Document.RoadAddress roadAddress = this.documents.get(0).getRoadAddress();
        Document.Address address = this.documents.get(0).getAddress();
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
}
