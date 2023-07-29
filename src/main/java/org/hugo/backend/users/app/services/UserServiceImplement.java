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

    /**
     * Crea un nuevo usuario en la base de datos.
     *
     * @param userRequestDTO El objeto UserRequestDTO que contiene los datos del usuario a crear.
     * @return Retorna el objeto UserResponseDTO que representa el usuario creado en la base de datos.
     * @throws MappingException Si ocurre un error durante la conversión entre DTO y entidad.
     */
    @Override
    @Transactional
    public UserResponseDTO createOne(UserRequestDTO userRequestDTO) throws MappingException {
        userRequestDTO.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        return DTOEntityMapper.convertEntityToDTO(userRepository.save(DTOEntityMapper.convertDTOToEntity(userRequestDTO,User.class)),
                UserResponseDTO.class);
    }

    /**
     * Actualiza un usuario existente en la base de datos.
     *
     * @param userRequestDTO El objeto UserRequestDTO que contiene los nuevos datos del usuario a actualizar.
     * @param id El ID del usuario que se va a actualizar.
     * @return Retorna el objeto UserResponseDTO que representa el usuario actualizado en la base de datos.
     * @throws MappingException Si ocurre un error durante la conversión entre DTO y entidad.
     * @throws PasswordUpdateNotAllowedException Si el usuario intenta actualizar la contraseña.
     */
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

    /**
     * Actualiza la contraseña de un usuario existente en la base de datos.
     *
     * @param userRequestDTO El objeto UserRequestDTO que contiene la nueva contraseña del usuario.
     * @param id El ID del usuario cuya contraseña se va a actualizar.
     * @return Retorna el objeto UserResponseDTO que representa el usuario actualizado en la base de datos.
     * @throws MappingException Si ocurre un error durante la conversión entre DTO y entidad.
     * @throws UpdateProfileNotAllowedException Si el usuario intenta actualizar otros datos además de la contraseña.
     */

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

    /**
     * Busca un usuario por su ID en la base de datos.
     *
     * @param id El ID del usuario que se va a buscar.
     * @return Retorna el objeto UserResponseDTO que representa el usuario encontrado en la base de datos.
     * @throws MappingException Si ocurre un error durante la conversión entre entidad y DTO.
     * @throws UserNotFoundException Si el usuario con el ID especificado no existe en la base de datos.
     */

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

    /**
     * Obtiene una lista de todos los usuarios en la base de datos.
     *
     * @return Retorna una lista de objetos UserResponseDTO que representa todos los usuarios en la base de datos.
     * @throws MappingException Si ocurre un error durante la conversión entre entidad y DTO.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() throws MappingException{
        return DTOEntityMapper.convertIterableToDTOList(userRepository.findAll(),UserResponseDTO.class);
    }

    /**
     * Elimina un usuario existente de la base de datos.
     *
     * @param id El ID del usuario que se va a eliminar.
     * @throws UserNotFoundException Si el usuario con el ID especificado no existe en la base de datos.
     */
    @Override
    @Transactional
    public void deleteOne(Long id) {
        findById(id);
        userRepository.deleteById(id);
    }
}
