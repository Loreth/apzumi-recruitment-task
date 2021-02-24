package pl.kamilprzenioslo.apzumi.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.kamilprzenioslo.apzumi.validation.PatchRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

  @JsonIgnore private Integer userId;

  @Null(groups = PatchRequest.class)
  private Integer id;

  private String title;
  private String body;
  @JsonIgnore private int version;
  @JsonIgnore private boolean deleted;
}
