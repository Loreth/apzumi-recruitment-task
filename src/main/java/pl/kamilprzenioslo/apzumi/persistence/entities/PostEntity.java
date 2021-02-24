package pl.kamilprzenioslo.apzumi.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

@Getter
@Setter
@ToString
@Entity
@Table(name = "post")
public class PostEntity implements Persistable<Integer> {

  private int userId;
  @Id private Integer id;
  private String title;
  private String body;
  @Version private int version;

  @Override
  public boolean isNew() {
    return version == 0;
  }
}
