package pl.kamilprzenioslo.apzumi.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;
import org.flywaydb.test.FlywayTestExecutionListener;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.kamilprzenioslo.apzumi.dtos.Post;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestExecutionListeners({
  DependencyInjectionTestExecutionListener.class,
  FlywayTestExecutionListener.class
})
class PostControllerIntegrationTest {
  @Autowired private WebTestClient webTestClient;
  @Autowired private JdbcTemplate jdbcTemplate;

  @FlywayTest
  @BeforeEach
  void setUp() {}

  @Test
  void whenGetAll_thenReturnAllNonDeletedPostsWithoutUserId() {
    webTestClient
        .get()
        .uri("/posts")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(Post.class)
        .consumeWith(
            result -> {
              List<Post> posts = result.getResponseBody();
              assertThat(posts, hasSize(3));
              assertThat(
                  posts.stream().map(Post::getUserId).collect(Collectors.toList()),
                  everyItem(nullValue()));
            });
  }

  @Test
  void givenExistentPostId_WhenGet_ThenReturnCorrectPostWithoutUserId() {
    webTestClient
        .get()
        .uri("/posts/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Post.class)
        .consumeWith(
            result -> {
              Post post = result.getResponseBody();
              assertNotNull(post);
              assertNull(post.getUserId());
              assertEquals(1, post.getId());
              assertEquals("great", post.getTitle());
              assertEquals("indeed great", post.getBody());
            });
  }

  @Test
  void givenDeletedPostId_WhenGet_ThenReturn404Status() {
    webTestClient.get().uri("/posts/{id}", 3).exchange().expectStatus().isNotFound();
  }

  @Test
  void givenNonExistentPostId_WhenGet_ThenReturn404Status() {
    webTestClient.get().uri("/posts/{id}", 222).exchange().expectStatus().isNotFound();
  }

  @Test
  void givenExistentPostId_WhenDelete_ThenIsProperlySoftDeleted() {
    webTestClient.delete().uri("/posts/{id}", 1).exchange().expectStatus().isOk();
    // direct query, as JPA shouldn't see the post anymore
    boolean deleted =
        jdbcTemplate.queryForObject("SELECT deleted FROM post WHERE id=1", Boolean.class);

    assertTrue(deleted);
    webTestClient.get().uri("/posts/{id}", 1).exchange().expectStatus().isNotFound();
  }

  @Test
  void givenCorrectDtoWithChanges_whenPatch_ThenPatchAndReturnPost() {
    webTestClient
        .get()
        .uri("/posts")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(Post.class)
        .consumeWith(
            result -> {
              List<Post> posts = result.getResponseBody();
              assertThat(posts, hasSize(3));
              assertThat(
                  posts.stream().map(Post::getUserId).collect(Collectors.toList()),
                  everyItem(nullValue()));
            });
  }
}
