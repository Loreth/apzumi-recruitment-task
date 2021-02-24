package pl.kamilprzenioslo.apzumi.services;

import java.util.List;
import java.util.Optional;
import pl.kamilprzenioslo.apzumi.dtos.Post;

public interface PostService {

  Optional<Post> findById(int id);

  /**
   * Returns posts, whose title contains provided value
   * @param title may be null
   * @return posts, whose title contains provided value
   */
  List<Post> findAll(String title);

  /**
   * Partially updates a Post, using non-null fields provided in a Post DTO
   * @param id id of a Post to be patched
   * @param post DTO containing modified fields
   * @return patched Post
   */
  Post patch(int id, Post post);

  void deleteById(int id);
}
