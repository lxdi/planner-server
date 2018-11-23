package model.dto;

public interface IMapper<D, E> {
    D mapToDto(E entity);
    E mapToEntity(D dto);

}
