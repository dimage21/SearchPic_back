package dimage.searchpic.domain.post;

import dimage.searchpic.domain.BaseEntity;
import dimage.searchpic.domain.location.Location;
import dimage.searchpic.domain.member.Member;
import dimage.searchpic.domain.posttag.PostTag;
import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.post.MaxTagSizeException;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Post extends BaseEntity {
    private static final int MAX_TAG_COUNT = 5;

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

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final List<PostTag> postTags = new ArrayList<>();

    public Post(Member author, Location location, String pictureUrl, String description,List<PostTag> postTags) {
        if (postTags != null) {
            setPostTags(postTags);
        }
        this.pictureUrl = pictureUrl;
        this.description = description;
        setAuthor(author);
        setLocation(location);
    }

    private void setPostTags(List<PostTag> postTags) {
        if (postTags.size() <= MAX_TAG_COUNT)
            this.postTags.addAll(postTags);
        else
            throw new MaxTagSizeException(ErrorInfo.MAX_TAG_SIZE_LIMIT);
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
        location.getPosts().remove(this);
    }
}