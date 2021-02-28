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
    postEntity = new PostEntity(1, 2, "a title", "some text");
  }

  @Test
  void givenSingleChange_WhenPatch_ThenPatchCorrectly() {
    Post patchPost = new Post();
    patchPost.setTitle("new title");

    postMapper.patchEntity(patchPost, postEntity);

    assertEquals(1, postEntity.getUserId());
    assertEquals(2, postEntity.getId());
    assertEquals("new title", postEntity.getTitle());
    assertEquals("some text", postEntity.getBody());
  }

  @Test
  void givenNoChanges_WhenPatch_ThenNothingIsChanged() {
    Post patchPost = new Post();

    postMapper.patchEntity(patchPost, postEntity);

    assertEquals(1, postEntity.getUserId());
    assertEquals(2, postEntity.getId());
    assertEquals("a title", postEntity.getTitle());
    assertEquals("some text", postEntity.getBody());
  }

  @Test
  void givenAllValuesChanged_whenPatch_ThenPatchCorrectly() {
    Post patchPost = new Post(10, 20, "new title", "NEW body!");

    postMapper.patchEntity(patchPost, postEntity);

    assertEquals(10, postEntity.getUserId());
    assertEquals(20, postEntity.getId());
    assertEquals("new title", postEntity.getTitle());
    assertEquals("NEW body!", postEntity.getBody());
  }
}
