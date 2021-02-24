package pl.kamilprzenioslo.apzumi.persistence.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pl.kamilprzenioslo.apzumi.dtos.Post;
import pl.kamilprzenioslo.apzumi.persistence.entities.PostEntity;

public interface PostRepository extends JpaRepository<PostEntity, Integer> {
  List<PostEntity> findByTitleContaining(String title);

  @Modifying
  @Query("""
  UPDATE PostEntity p
  SET p.userId = :#{#post.userId}, p.title = :#{#post.title}, p.body = :#{#post.body}
  WHERE p.id = :#{#post.id} AND p.version = 1
  """)
  void updatePostIfUnmodified(Post post);
}
