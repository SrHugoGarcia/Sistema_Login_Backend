package org.hugo.backend.users.app.utils;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DTOEntityMapper {
    private static final ModelMapper modelMapper = new ModelMapper();
    private DTOEntityMapper() {
        // Clase de utilidad, no necesita ser instanciada.
    }

    public static <D, E> D convertEntityToDTO(E entity, Class<D> dtoClass) {
        return modelMapper.map(entity, dtoClass);
    }

    public static <D, E> E convertDTOToEntity(D dto, Class<E> entityClass) {
        return modelMapper.map(dto, entityClass);
    }
    public static <D, E> List<D> convertIterableToDTOList(Iterable<E> entityIterable, Class<D> dtoClass) {
        return StreamSupport.stream(entityIterable.spliterator(), false)
                .map(entity -> modelMapper.map(entity, dtoClass))
                .collect(Collectors.toList());
    }
}