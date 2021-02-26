package pl.kamilprzenioslo.apzumi.persistence.repositories;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import pl.kamilprzenioslo.apzumi.persistence.entities.PostEntity;

@DataJpaTest
class PostRepositoryTest {
  @Autowired PostRepository postRepository;

  @Test
  void givenPosts_WhenUpdateUnmodifiedPosts_ThenUpdateCorrectly() {
    List<PostEntity> posts =
        List.of(
            new PostEntity(1, 1, "aaa", "AAA"),
            new PostEntity(1, 2, "bbb", "BBB"),
            new PostEntity(555, 3, "ccc", "CCC"),
            new PostEntity(2, 4, "ddd", "DDD"),
            new PostEntity(3, 5, "eee", "EEE"));

    posts.forEach(postRepository::updatePostIfUnmodified);
    List<PostEntity> postEntities = postRepository.findAll(Sort.by("id"));

    assertThat(postEntities, hasSize(3));
    assertEquals("aaa", postEntities.get(0).getTitle());
    assertEquals("AAA", postEntities.get(0).getBody());
    assertEquals("even better", postEntities.get(1).getTitle());
    assertEquals("or not", postEntities.get(1).getBody());
    assertEquals("eee", postEntities.get(2).getTitle());
    assertEquals("EEE", postEntities.get(2).getBody());
    assertEquals(5, postEntities.get(2).getId());
  }
}
