package pl.kamilprzenioslo.apzumi.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Post {

  @JsonIgnore private Integer userId;
  private Integer id;
  private String title;
  private String body;
  @JsonIgnore private int version;
  @JsonIgnore private boolean deleted;
}
