package pl.kamilprzenioslo.apzumi.services.impl;

import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import pl.kamilprzenioslo.apzumi.dtos.Post;
import pl.kamilprzenioslo.apzumi.mappers.PostMapper;
import pl.kamilprzenioslo.apzumi.persistence.entities.PostEntity;
import pl.kamilprzenioslo.apzumi.persistence.repositories.PostRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

  private static final MockWebServer mockWebServer = new MockWebServer();
  private final ObjectMapper objectMapper = new ObjectMapper();
  @Mock private PostRepository postRepository;
  @Mock private PostMapper postMapper;
  private PostServiceImpl postService;
  private final List<PostEntity> publicApiPosts =
      List.of(
          new PostEntity(1, 1, "aaa", "AAA"),
          new PostEntity(1, 2, "bbb", "BBB"),
          new PostEntity(4, 3, "ccc", "CCC"),
          new PostEntity(2, 4, "ddd", "DDD"),
          new PostEntity(3, 5, "eee", "EEE"));

  @BeforeEach
  void setUp() {
    postService =
        new PostServiceImpl(
            postRepository, postMapper, WebClient.builder(), mockWebServer.url("/").toString());
  }

  @BeforeAll
  static void setup() throws IOException {
    mockWebServer.start();
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void givenExistentPost_WhenPatch_ThenEntityIsMarkedAsModifiedByUser() {
    Post postWithChanges = new Post(null, null, "c", "d");
    PostEntity postEntity = new PostEntity(1, 1, "a", "b");

    when(postRepository.findById(1)).thenReturn(Optional.of(postEntity));
    postService.patch(1, postWithChanges);

    verify(postRepository).save(argThat(PostEntity::isModifiedByUser));
  }

  @Test
  void givenPosts_WhenFetchAndUpdateUnmodifiedPosts_ThenAllPostsGetPassedToRepository()
      throws JsonProcessingException {
    MockResponse mockResponse =
        new MockResponse()
            .setResponseCode(HttpStatus.OK.value())
            .setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
            .setBody(objectMapper.writeValueAsString(publicApiPosts));
    mockWebServer.enqueue(mockResponse);

    postService.fetchAndUpdateUnmodifiedPosts();

    verify(postRepository, timeout(2000).times(5))
        .updatePostIfUnmodified(argThat(publicApiPosts::contains));
  }

  @Test
  void givenPosts_WhenFetchAndReplaceAllPosts_ThenAllPostsGetReplaced()
      throws JsonProcessingException {
    MockResponse mockResponse =
        new MockResponse()
            .setResponseCode(HttpStatus.OK.value())
            .setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
            .setBody(objectMapper.writeValueAsString(publicApiPosts));
    mockWebServer.enqueue(mockResponse);

    postService.fetchAndReplaceAllPosts();

    verify(postRepository, timeout(2000)).deleteAllInBatch();
    verify(postRepository, timeout(2000).times(5)).save(argThat(publicApiPosts::contains));
  }
}
