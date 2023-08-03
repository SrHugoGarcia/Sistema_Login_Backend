package org.hugo.backend.users.app.services;

import org.hugo.backend.users.app.controllers.dto.UserRequestDTO;
import org.hugo.backend.users.app.controllers.dto.UserResponseDTO;
import org.hugo.backend.users.app.exceptions.user.*;
import org.hugo.backend.users.app.repositories.RoleRepository;
import org.hugo.backend.users.app.repositories.UserRepository;
import org.hugo.backend.users.app.models.entities.User;
import org.hugo.backend.users.app.utils.DTOEntityMapper;
import org.hugo.backend.users.app.utils.PaginatedResponse;
import org.hugo.backend.users.app.utils.SpecificationBuilder;
import org.hugo.backend.users.app.utils.OrderType;
import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;


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
    private static final String PAGE_NUMBER_NEGATIVE_ERROR = "El número de página no puede ser negativo.";
    private static final String LIMIT_GREATER_THAN_ZERO_MSG = "El límite de resultados debe ser mayor que 0.";
    private static final String INVALID_SORTING_ORDER_MSG = "El ordenamiento es inválido.";

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
     * Recupera una lista paginada de usuarios ordenada según los parámetros proporcionados.
     *
     * @param pageNumber El número de página a recuperar (debe ser no negativo).
     * @param resultsLimit El límite de resultados por página (debe ser mayor que 0).
     * @param sortBy El campo por el que se ordenarán los resultados.
     * @param orderType El tipo de orden (puede ser "ascendente" o "descendente").
     * @param fields Lista de campos a incluir en los objetos UserResponseDTO.
     *               Los campos no válidos serán ignorados en la respuesta.
     * @param filters Lista de filtros en formato "campo:operador:valor".
     *                Los filtros se aplicarán a los campos correspondientes usando los operadores especificados.
     * @return Una lista de objetos UserResponseDTO.
     * @throws NegativePageNumberException si el número de página es negativo.
     * @throws NegativeLimitNumberException si el límite de resultados es no positivo.
     * @throws OrderInvalidException si el tipo de orden no es válido.
     */
    @Override
    public PaginatedResponse<UserResponseDTO> findAll(int pageNumber, int resultsLimit,
                                         String sortBy, String orderType, List<String> fields,List<String> filters)
            throws MappingException {
        List<User> users = null;
        if (pageNumber < 0) {
            throw new NegativePageNumberException(PAGE_NUMBER_NEGATIVE_ERROR);
        }

        if (resultsLimit <= 0) {
            throw new NegativeLimitNumberException(LIMIT_GREATER_THAN_ZERO_MSG);
        }

        Pageable pageable;
        OrderType order = OrderType.fromValue(orderType.toLowerCase());
        if (order == OrderType.ASCENDING) {
            pageable = PageRequest.of(pageNumber, resultsLimit, Sort.by(sortBy).ascending());
        } else if (order == OrderType.DESCENDING) {
            pageable = PageRequest.of(pageNumber, resultsLimit, Sort.by(sortBy).descending());
        } else {
            throw new OrderInvalidException(INVALID_SORTING_ORDER_MSG);
        }

        SpecificationBuilder<User> userSpecificationBuilder = new SpecificationBuilder<>();
        Specification<User> spec = userSpecificationBuilder.buildSpecification(filters);
        // Crear un objeto PaginatedResponse y configurar sus propiedades
        PaginatedResponse<UserResponseDTO> paginatedResponse = new PaginatedResponse<>();

        if (spec != null) {
            Page<User> userPage = userRepository.findAll(spec, pageable);
            users = userPage.stream().toList();
            paginatedResponse.setTotalPages(userPage.getTotalPages());
            paginatedResponse.setTotalElements(userPage.getTotalElements());
            paginatedResponse.setCurrentPage(pageNumber);
        } else {
            Page<User> userPage = userRepository.findAll(pageable);
            users = userPage.stream().toList();
            paginatedResponse.setTotalPages(userPage.getTotalPages());
            paginatedResponse.setTotalElements(userPage.getTotalElements());
            paginatedResponse.setCurrentPage(pageNumber);
        }

        List<UserResponseDTO> responseDTOs = DTOEntityMapper.convertIterableToDTOList(users, UserResponseDTO.class);
        if (fields != null && !fields.isEmpty()) {
            responseDTOs = responseDTOs.stream().map(dto -> {
                dto.filterFieldsAndRemove(fields);
                if(fields.isEmpty()){
                    return dto;
                }
                return dto.filterFields(fields);
            }).collect(Collectors.toList());
        }

        paginatedResponse.setResults(responseDTOs);

        return paginatedResponse;
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
