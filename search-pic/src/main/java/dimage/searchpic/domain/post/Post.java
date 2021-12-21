package dimage.searchpic.domain.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dimage.searchpic.domain.BaseEntity;
import dimage.searchpic.domain.location.Location;
import dimage.searchpic.domain.member.Member;
import dimage.searchpic.domain.posttag.PostTag;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@DynamicUpdate
public class Post extends BaseEntity {
    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member author;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    private String pictureUrl;

    @ColumnDefault("0")
    private Long view = 0L;

    @Lob
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<PostTag> postTags = new ArrayList<>();

    @Builder
    public Post(Member author, Location location, String pictureUrl, String description) {
        this.pictureUrl = pictureUrl;
        this.description = description;
        setAuthor(author);
        setLocation(location);
    }

    public void setPostTags(List<PostTag> postTags) {
        this.postTags = postTags;
    }

    public void setAuthor(Member author) {
        this.author = author;
        author.getPosts().add(this);
        author.addPostCount();
    }

    public void setLocation(Location location) {
        this.location = location;
        location.getPosts().add(this);
    }

    // 글 조회수 증가
    public void addView() {
        this.view++;
    }

    public void delete() {
        author.getPosts().remove(this);
        author.reducePostCount();
        location.getPosts().remove(this);
    }

    public void updatePost(List<PostTag> updateTags, String description) {
        this.description = description;
        if (updateTags != null) {
            this.postTags.clear();
            this.postTags.addAll(updateTags);
        }
    }
}