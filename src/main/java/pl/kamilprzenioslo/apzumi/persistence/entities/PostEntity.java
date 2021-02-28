package pl.kamilprzenioslo.apzumi.persistence.entities;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "post")
public class PostEntity implements Persistable<Integer> {

  private int userId;
  @Id private Integer id;
  private String title;
  private String body;
  @Version private Integer version;
  private boolean modifiedByUser;

  public PostEntity(int userId, Integer id, String title, String body) {
    this.userId = userId;
    this.id = id;
    this.title = title;
    this.body = body;
  }

  @Override
  public boolean isNew() {
    return version == null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PostEntity that = (PostEntity) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, id);
  }
}
