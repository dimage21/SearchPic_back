package dimage.searchpic.domain.location;

import dimage.searchpic.domain.post.Post;
import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class Location {
    @Id
    @Column(name = "location_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double x;
    private double y;
    private String dong;
    private String si;
    private String gu;
    private String address;
    private String placeName;

    @OneToMany(mappedBy = "location")
    List<Post> posts = new ArrayList<>();

    private String repImageUrl;

    @Builder
    public Location(double x, double y, String dong, String si, String gu,
                    String address,String placeName, List<Post> posts,String repImage) {
        this.x = x;
        this.y = y;
        this.dong = dong;
        this.si = si;
        this.gu = gu;
        this.address = address;
        this.placeName = placeName;
        this.posts = posts;
        this.repImageUrl = repImage;
    }

    public boolean coordinatesIsEqual(Location marked, Location cur) {
        if (marked.x == cur.x && marked.y == cur.y) {
            return true;
        }
        return false;
    }
}