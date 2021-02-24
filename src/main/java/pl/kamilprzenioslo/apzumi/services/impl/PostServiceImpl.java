package pl.kamilprzenioslo.apzumi.services.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.kamilprzenioslo.apzumi.dtos.Post;
import pl.kamilprzenioslo.apzumi.mappers.PostMapper;
import pl.kamilprzenioslo.apzumi.persistence.entities.PostEntity;
import pl.kamilprzenioslo.apzumi.persistence.repositories.PostRepository;
import pl.kamilprzenioslo.apzumi.services.PostService;
import reactor.core.publisher.Flux;

@Service
public class PostServiceImpl implements PostService {
  private static final String POSTS_API_URL = "https://jsonplaceholder.typicode.com/posts";
  private final PostRepository repository;
  private final PostMapper mapper;
  private final WebClient webClient;

  public PostServiceImpl(
      PostRepository repository, PostMapper mapper, WebClient.Builder webClientBuilder) {
    this.repository = repository;
    this.mapper = mapper;
    this.webClient = webClientBuilder.build();
  }

  public Optional<Post> findById(int id) {
    return repository.findById(id).map(mapper::mapToDto);
  }

  @Override
  public List<Post> findAll(String title) {
    return mapper.mapToDtos(repository.findByTitleContaining(title));
  }

  @Override
  public Post patch(int id, Post post) {
    PostEntity postEntity = repository.findById(id).orElseThrow();
    mapper.patchEntity(post, postEntity);
    repository.save(postEntity);

    return mapper.mapToDto(postEntity);
  }

  @Override
  public void deleteById(int id) {
    repository.deleteById(id);
  }

  @Scheduled(cron = "${app.post-update-cron-schedule}")
  private void fetchAndUpdateUnmodifiedPosts() {
    Flux<Post> postFlux =
        webClient
            .get()
            .uri(POSTS_API_URL)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(Post.class);

    postFlux.subscribe(repository::updatePostIfUnmodified);
  }
}
