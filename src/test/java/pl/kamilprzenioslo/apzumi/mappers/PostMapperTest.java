package pl.kamilprzenioslo.apzumi.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.kamilprzenioslo.apzumi.dtos.Post;
import pl.kamilprzenioslo.apzumi.persistence.entities.PostEntity;

class PostMapperTest {
  private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);
  private PostEntity postEntity;

  @BeforeEach
  void setUp() {
    postEntity = new PostEntity();
    postEntity.setId(1);
    postEntity.setUserId(2);
    postEntity.setTitle("a title");
    postEntity.setBody("some text");
  }

  @Test
  void patchWithSingleChangePatchesCorrectly() {
    Post patchPost = new Post();
    patchPost.setTitle("new title");

    postMapper.patchEntity(patchPost, postEntity);

    assertEquals(1, postEntity.getId());
    assertEquals(2, postEntity.getUserId());
    assertEquals("new title", postEntity.getTitle());
    assertEquals("some text", postEntity.getBody());
  }

  @Test
  void patchWithNoChangesDoesntChangeAnything() {
    Post patchPost = new Post();

    postMapper.patchEntity(patchPost, postEntity);

    assertEquals(1, postEntity.getId());
    assertEquals(2, postEntity.getUserId());
    assertEquals("a title", postEntity.getTitle());
    assertEquals("some text", postEntity.getBody());
  }

  @Test
  void patchWithAllValuesChangedPatchesCorrectly() {
    Post patchPost = new Post();
    patchPost.setId(20);
    patchPost.setUserId(10);
    patchPost.setTitle("new title");
    patchPost.setBody("NEW body!");

    postMapper.patchEntity(patchPost, postEntity);

    assertEquals(20, postEntity.getId());
    assertEquals(10, postEntity.getUserId());
    assertEquals("new title", postEntity.getTitle());
    assertEquals("NEW body!", postEntity.getBody());
  }
}
