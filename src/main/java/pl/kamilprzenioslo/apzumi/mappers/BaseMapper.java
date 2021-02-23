package pl.kamilprzenioslo.apzumi.mappers;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import java.util.Collection;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;

public interface BaseMapper<D, E> {

  D mapToDto(E entity);

  E mapToEntity(D dto);

  List<D> mapToDtos(Collection<E> entities);

  @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
  void patchEntity(D dto, @MappingTarget E entity);
}
