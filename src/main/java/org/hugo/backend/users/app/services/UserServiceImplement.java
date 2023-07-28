package org.hugo.backend.users.app.services;

import org.hugo.backend.users.app.controllers.dto.UserRequestDTO;
import org.hugo.backend.users.app.controllers.dto.UserResponseDTO;
import org.hugo.backend.users.app.exceptions.user.PasswordUpdateNotAllowedException;
import org.hugo.backend.users.app.exceptions.user.UpdateProfileNotAllowedException;
import org.hugo.backend.users.app.exceptions.user.UserNotFoundException;
import org.hugo.backend.users.app.repositories.RoleRepository;
import org.hugo.backend.users.app.repositories.UserRepository;
import org.hugo.backend.users.app.models.entities.User;
import org.hugo.backend.users.app.utils.DTOEntityMapper;
import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Service
public class UserServiceImplement implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;
    private static final String PASSWORD_UPDATE_NOT_ALLOWED_MSG = "No se permite actualizar la contraseña para el usuario con ID: ";
    private static final String PASSWORD_UPDATE_ONLY_MSG = "Solo se permite actualizar la contraseña";
    private static final String USER_NOT_FOUND_MSG = " no existe en la base de datos";
    private static final String ID_ERROR_MSG = "Error: el ID: ";

    @Override
    @Transactional
    public UserResponseDTO createOne(UserRequestDTO userRequestDTO) throws MappingException {
        userRequestDTO.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        return DTOEntityMapper.convertEntityToDTO(userRepository.save(DTOEntityMapper.convertDTOToEntity(userRequestDTO,User.class)),
                UserResponseDTO.class);
    }

    @Override
    @Transactional
    public UserResponseDTO updateOne(UserRequestDTO userRequestDTO,Long id) throws MappingException{
        User existingUser =DTOEntityMapper.convertDTOToEntity(findById(id),User.class);
        existingUser.setName(userRequestDTO.getName());
        existingUser.setLastname(userRequestDTO.getLastname());
        existingUser.setEmail(userRequestDTO.getEmail());
        existingUser.setRoles(userRequestDTO.getRoles());
        if(userRequestDTO.getPassword() != null){
            throw new PasswordUpdateNotAllowedException(PASSWORD_UPDATE_NOT_ALLOWED_MSG + existingUser.getId());
        }
        return DTOEntityMapper.convertEntityToDTO(userRepository.save(existingUser),UserResponseDTO.class);
    }


    @Override
    @Transactional
    public UserResponseDTO updatePassword(UserRequestDTO userRequestDTO, Long id) throws MappingException {
        User existingUser = DTOEntityMapper.convertDTOToEntity(findById(id),User.class);
        existingUser.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        if(userRequestDTO.getName() != null ||
                userRequestDTO.getLastname() != null ||
                userRequestDTO.getEmail() != null){
            throw new UpdateProfileNotAllowedException(PASSWORD_UPDATE_ONLY_MSG);
        }
        return DTOEntityMapper
                .convertEntityToDTO(userRepository.save(existingUser),UserResponseDTO.class);
    }


    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id)throws MappingException {
        User user = userRepository.findById(id).orElse(null);
        if(user == null){
            throw new UserNotFoundException(ID_ERROR_MSG
                    .concat(id.toString())
                    .concat(USER_NOT_FOUND_MSG));
        }
        return DTOEntityMapper.convertEntityToDTO(user,UserResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() throws MappingException{
        return DTOEntityMapper.convertIterableToDTOList(userRepository.findAll(),UserResponseDTO.class);
    }

    @Override
    @Transactional
    public void deleteOne(Long id) {
        findById(id);
        userRepository.deleteById(id);
    }
}
