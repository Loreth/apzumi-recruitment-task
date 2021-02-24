package pl.kamilprzenioslo.apzumi.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.domain.Persistable;

/**
 * This entity implements soft delete due to a business requirement of not updating deleted posts,
 * meanwhile preserving the ability to handle potential new posts.
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "post")
@SQLDelete(sql = "UPDATE post SET deleted=true WHERE id=? and version = ?")
@Where(clause = "deleted = false")
public class PostEntity implements Persistable<Integer> {

  private int userId;
  @Id private Integer id;
  private String title;
  private String body;
  @Version private int version;
  private boolean deleted;

  @Override
  public boolean isNew() {
    return version == 0;
  }
}
