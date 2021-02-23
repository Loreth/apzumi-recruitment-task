package pl.kamilprzenioslo.apzumi.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kamilprzenioslo.apzumi.persistence.entities.PostEntity;

public interface PostRepository extends JpaRepository<PostEntity, Integer> {}
