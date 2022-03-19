package dimage.searchpic.domain.posttag;

import dimage.searchpic.domain.post.Post;
import dimage.searchpic.domain.tag.Tag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PostTag {
    @Id
    @Column(name = "post_tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public PostTag(Post post, Tag tag) {
        this.post = post;
        this.tag = tag;
        post.getPostTags().add(this);
        tag.getPostTags().add(this);
    }

    public static PostTag create(Post post, Tag tag) {
        return new PostTag(post, tag);
    }
}
