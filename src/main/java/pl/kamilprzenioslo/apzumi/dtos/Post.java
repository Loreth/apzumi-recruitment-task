package pl.kamilprzenioslo.apzumi.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.Null;
import lombok.Data;
import pl.kamilprzenioslo.apzumi.validation.PatchRequest;

@Data
public class Post {

  @JsonIgnore private Integer userId;

  @Null(groups = PatchRequest.class)
  private Integer id;

  private String title;
  private String body;
  @JsonIgnore private int version;
  @JsonIgnore private boolean deleted;
}
