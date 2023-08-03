package org.hugo.backend.users.app.services;

import org.hugo.backend.users.app.controllers.dto.UserRequestDTO;
import org.hugo.backend.users.app.controllers.dto.UserResponseDTO;
import org.hugo.backend.users.app.utils.PaginatedResponse;
import org.modelmapper.MappingException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserResponseDTO createOne(UserRequestDTO userRequestDTO) throws MappingException;
    UserResponseDTO updateOne(UserRequestDTO userRequestDTO, Long id) throws MappingException;
    UserResponseDTO findById(Long id) throws MappingException;
    List<UserResponseDTO> findAll() throws MappingException;
    PaginatedResponse<UserResponseDTO> findAll(int pageNumber, int resultsLimit, String sortBy, String orderType, List<String> fields, List<String> filters) throws MappingException;
    void deleteOne(Long id);
    UserResponseDTO updatePassword(UserRequestDTO userRequestDTO,Long id) throws MappingException;
}
