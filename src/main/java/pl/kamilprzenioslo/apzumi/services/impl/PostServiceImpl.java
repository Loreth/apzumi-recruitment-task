package pl.kamilprzenioslo.apzumi.services.impl;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import pl.kamilprzenioslo.apzumi.dtos.Post;
import pl.kamilprzenioslo.apzumi.exceptions.EntityNotFoundException;
import pl.kamilprzenioslo.apzumi.mappers.PostMapper;
import pl.kamilprzenioslo.apzumi.persistence.entities.PostEntity;
import pl.kamilprzenioslo.apzumi.persistence.repositories.PostRepository;
import pl.kamilprzenioslo.apzumi.services.PostService;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class PostServiceImpl implements PostService {

  private static final String ENTITY_NOT_FOUND_MESSAGE = "Entity does not exist with id=";
  private final PostRepository repository;
  private final PostMapper mapper;
  private final WebClient webClient;

  public PostServiceImpl(
      PostRepository repository,
      PostMapper mapper,
      Builder webClientBuilder,
      @Value("${post-api-url}") String postApiUrl) {
    this.repository = repository;
    this.mapper = mapper;
    this.webClient = webClientBuilder.baseUrl(postApiUrl).build();
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
    PostEntity postEntity =
        repository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND_MESSAGE + id));

    postEntity.setModifiedByUser(true);
    mapper.patchEntity(post, postEntity);
    repository.save(postEntity);

    return mapper.mapToDto(postEntity);
  }

  @Override
  public void deleteById(int id) {
    if (repository.existsById(id)) {
      repository.deleteById(id);
    } else {
      throw new EntityNotFoundException(ENTITY_NOT_FOUND_MESSAGE + id);
    }
  }

  @Async
  @Scheduled(cron = "${scheduling.post-update.cron}")
  public void fetchAndUpdateUnmodifiedPosts() {
    Flux<PostEntity> postFlux =
        webClient.get().accept(MediaType.APPLICATION_JSON).retrieve().bodyToFlux(PostEntity.class);

    log.debug("Updating unmodified posts");
    postFlux.subscribe(repository::updatePostIfUnmodified);
  }

  @Async
  @Transactional
  public void fetchAndReplaceAllPosts() {
    fetchPostsFromPublicApi().doFirst(repository::deleteAllInBatch).subscribe(repository::save);
  }

  private Flux<PostEntity> fetchPostsFromPublicApi() {
    log.debug("Fetching posts from public API");
    Flux<PostEntity> postFlux =
        webClient
            .get()
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(PostEntity.class)
            .publish()
            .autoConnect(2);

    postFlux.count().subscribe(count -> log.debug("Fetched {} posts", count));

    return postFlux;
  }
}
