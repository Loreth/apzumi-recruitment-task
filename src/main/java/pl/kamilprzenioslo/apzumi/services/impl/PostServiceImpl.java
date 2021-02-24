package pl.kamilprzenioslo.apzumi.services.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kamilprzenioslo.apzumi.dtos.Post;
import pl.kamilprzenioslo.apzumi.mappers.PostMapper;
import pl.kamilprzenioslo.apzumi.persistence.entities.PostEntity;
import pl.kamilprzenioslo.apzumi.persistence.repositories.PostRepository;
import pl.kamilprzenioslo.apzumi.services.PostService;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {
  private final PostRepository repository;
  private final PostMapper mapper;

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
}
