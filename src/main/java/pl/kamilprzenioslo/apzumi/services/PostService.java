package pl.kamilprzenioslo.apzumi.services;

import java.util.List;
import java.util.Optional;
import pl.kamilprzenioslo.apzumi.dtos.Post;

public interface PostService {

  Optional<Post> findById(int id);

  List<Post> findAll(String title);

  Post patch(int id, Post post);

  void deleteById(int id);
}
