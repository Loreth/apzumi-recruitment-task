package pl.kamilprzenioslo.apzumi.mappers;

import org.mapstruct.Mapper;
import pl.kamilprzenioslo.apzumi.dtos.Post;
import pl.kamilprzenioslo.apzumi.persistence.entities.PostEntity;

@Mapper
public interface PostMapper extends BaseMapper<Post, PostEntity> {}
