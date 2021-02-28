package pl.kamilprzenioslo.apzumi.mappers;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import java.util.Collection;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;

/**
 * Basic mapper for conversion between a DTO and an Entity.
 *
 * @param <D> type of DTO
 * @param <E> type of Entity
 */
public interface BaseMapper<D, E> {

  D mapToDto(E entity);

  List<D> mapToDtos(Collection<E> entities);

  @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
  void patchEntity(D dto, @MappingTarget E entity);
}
