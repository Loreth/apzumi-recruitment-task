package pl.kamilprzenioslo.apzumi.persistence.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.kamilprzenioslo.apzumi.persistence.entities.PostEntity;

public interface PostRepository extends JpaRepository<PostEntity, Integer> {
  List<PostEntity> findByTitleContaining(String title);
}
