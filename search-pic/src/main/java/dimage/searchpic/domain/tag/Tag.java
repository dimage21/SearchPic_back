package dimage.searchpic.domain.tag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dimage.searchpic.domain.posttag.PostTag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return tag.name.equals(name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
