package pl.kamilprzenioslo.apzumi.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import javax.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.kamilprzenioslo.apzumi.validation.PatchRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

  @JsonProperty(access = Access.WRITE_ONLY)
  private Integer userId;

  @Null(groups = PatchRequest.class)
  private Integer id;

  private String title;
  private String body;
}
