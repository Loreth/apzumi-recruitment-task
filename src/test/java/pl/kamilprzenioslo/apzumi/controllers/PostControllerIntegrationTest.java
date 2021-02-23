package pl.kamilprzenioslo.apzumi.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.kamilprzenioslo.apzumi.dtos.Post;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql("/test-data.sql")
class PostControllerIntegrationTest {
  @Autowired private WebTestClient webTestClient;

  @BeforeEach
  void setUp() {}

  @Test
  void forExistingIdGetReturnsCorrectPostWithoutUserId() {
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
  void getAllReturnsAllNotDeletedPostsWithoutUserId() {
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
  void getReturns404StatusForDeletedPost() {}

  @Test
  void patch() {}

  @Test
  void delete() {}
}
