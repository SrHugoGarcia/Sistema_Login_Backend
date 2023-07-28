package org.hugo.backend.users.app.services;

import org.hugo.backend.users.app.controllers.dto.UserRequestDTO;
import org.hugo.backend.users.app.controllers.dto.UserResponseDTO;
import org.hugo.backend.users.app.models.entities.User;
import org.hugo.backend.users.app.repositories.UserRepository;
import org.modelmapper.MappingException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

public interface UserService {
    UserResponseDTO createOne(UserRequestDTO userRequestDTO) throws MappingException;
    UserResponseDTO updateOne(UserRequestDTO userRequestDTO, Long id) throws MappingException;
    UserResponseDTO findById(Long id) throws MappingException;
    List<UserResponseDTO> findAll() throws MappingException;
    void deleteOne(Long id);
    UserResponseDTO updatePassword(UserRequestDTO userRequestDTO,Long id) throws MappingException;
}
