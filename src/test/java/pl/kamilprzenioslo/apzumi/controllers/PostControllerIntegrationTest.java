package pl.kamilprzenioslo.apzumi.controllers;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.kamilprzenioslo.apzumi.dtos.Post;
import pl.kamilprzenioslo.apzumi.persistence.repositories.PostRepository;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = "scheduling.enabled=true")
@TestExecutionListeners({
  DependencyInjectionTestExecutionListener.class,
  FlywayTestExecutionListener.class
})
class PostControllerIntegrationTest {
  @Autowired private WebTestClient webTestClient;
  @Autowired private PostRepository postRepository;

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
  void givenExistentPostId_WhenDelete_ThenIsDeletedAndReturns200Status() {
    webTestClient.delete().uri("/posts/{id}", 1).exchange().expectStatus().isOk();
    boolean deleted = !postRepository.existsById(1);

    assertTrue(deleted);
  }

  @Test
  void givenNonExistentPostId_WhenDelete_ThenReturn404Status() {
    webTestClient.delete().uri("/posts/{id}", 222).exchange().expectStatus().isNotFound();
  }

  @Test
  void givenCorrectDtoWithChanges_WhenPatch_ThenPatchAndReturnPost() {
    Post patchPost = new Post();
    patchPost.setBody("fancy new body");
    patchPost.setTitle("upgraded title");

    webTestClient
        .patch()
        .uri("/posts/{id}", 5)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(patchPost)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Post.class)
        .consumeWith(
            result -> {
              Post post = result.getResponseBody();
              assertNotNull(post);
              assertEquals(5, post.getId());
              assertEquals("fancy new body", post.getBody());
              assertEquals("upgraded title", post.getTitle());
            });
  }

  @Test
  void givenDtoWithIdChange_WhenPatch_ThenReturn400Status() {
    Post patchPost = new Post();
    patchPost.setId(3);

    webTestClient
        .patch()
        .uri("/posts/{id}", 1)
        .bodyValue(patchPost)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void givenTitleFragment_WhenGet_ThenReturnPostsContainingTitle() {
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/posts").queryParam("title", "re").build())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(Post.class)
        .consumeWith(
            result -> {
              List<Post> foundPosts = result.getResponseBody();
              assertNotNull(foundPosts);
              assertThat(foundPosts, hasSize(2));

              List<String> postTitles =
                  foundPosts.stream().map(Post::getTitle).collect(Collectors.toList());

              assertThat(postTitles, everyItem(containsString("re")));
            });
  }

  @Test
  void whenFetchPosts_ThenReturn202StatusAndFetchAndReplaceAllPosts() {
    webTestClient
        .post()
        .uri(uriBuilder -> uriBuilder.path("/posts/fetch-requests").build())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isAccepted();

    await().atMost(Duration.ofSeconds(3)).until(() -> postRepository.count(), equalTo(100L));
    await()
        .atMost(Duration.ofSeconds(3))
        .until(() -> postRepository.findById(1).orElseThrow().getTitle(), not("great"));
    await()
        .atMost(Duration.ofSeconds(3))
        .until(() -> postRepository.findById(2).orElseThrow().getTitle(), not("even better"));
    await()
        .atMost(Duration.ofSeconds(3))
        .until(() -> postRepository.findById(5).orElseThrow().getTitle(), not("Hello there!"));
  }
}
