package pl.kamilprzenioslo.apzumi.persistence.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import pl.kamilprzenioslo.apzumi.persistence.entities.PostEntity;

public interface PostRepository extends JpaRepository<PostEntity, Integer> {
  List<PostEntity> findByTitleContaining(String title);

  @Transactional
  @Modifying
  @Query("""
  UPDATE PostEntity p
  SET p.userId = :#{#post.userId}, p.title = :#{#post.title}, p.body = :#{#post.body}, p.version = p.version+1
  WHERE p.id = :#{#post.id} AND p.modifiedByUser = false
  """)
  void updatePostIfUnmodified(PostEntity post);
}
