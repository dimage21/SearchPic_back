package dimage.searchpic.domain.tag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dimage.searchpic.domain.posttag.PostTag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Tag {

    @Id
    @Column(name = "tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "tag")
    private final List<PostTag> postTags = new ArrayList<>();

    public Tag(String name) {
        this.name = name;
    }

    public static Tag create(String name) {
        return new Tag(name);
    }
}