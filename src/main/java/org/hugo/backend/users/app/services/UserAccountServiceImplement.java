package org.hugo.backend.users.app.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hugo.backend.users.app.auth.TokenJwtConfig;
import org.hugo.backend.users.app.controllers.dto.UserAccountRequestDTO;
import org.hugo.backend.users.app.controllers.dto.UserAccountResponseDTO;
import org.hugo.backend.users.app.exceptions.role.RoleNotFoundException;
import org.hugo.backend.users.app.exceptions.user.*;
import org.hugo.backend.users.app.models.entities.Role;
import org.hugo.backend.users.app.models.entities.User;
import org.hugo.backend.users.app.repositories.RoleRepository;
import org.hugo.backend.users.app.repositories.UserRepository;
import org.hugo.backend.users.app.utils.DTOEntityMapper;
import org.hugo.backend.users.app.utils.TemplateType;
import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
public class UserAccountServiceImplement implements UserAccountService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private static final long TOKEN_EXPIRATION_TIME_FORGET_PASSWORD = 20L * 60L * 1000L;
    // Ejemplo:
    private static final String UPDATE_PASSWORD_ERROR_MSG = "Solo se permite actualizar la contraseña para el usuario con ID: ";
    private static final String TOKEN_EXPIRATION_EMAIL_SUBJECT = "Recuperacion de contraseña expira en ";
    private static final String TOKEN_EXPIRATION_EMAIL_BODY = " minutos";
    private static final String ERROR_SENDING_EMAIL_MSG = "Error al enviar el correo electrónico: ";
    private static final String PASSWORD_UPDATE_NOT_ALLOWED_MSG = "No se permite actualizar la contraseña para el usuario con ID: ";
    private static final String ROLE_NOT_FOUND_MSG = "Error: el role: user no existe en la base de datos.";
    private static final String TOKEN_NOT_EXPIRED_MSG = "El token no ha expirado, verifica la bandeja de entrada o spam de tu correo";
    private static final String EMAIL_NOT_FOUND_MSG = "El email no existe, ingresa uno valido.";
    private static final String ACCESS_RESOURCE_LOGIN_MSG = "Inicia sesión para poder acceder a este recurso";
    private static final String MALFORMED_TOKEN_MSG = "Token malformado";
    private static final String SENDER_EMAIL = "org.hugo@gmail.com";

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param userAccountRequestDTO Contiene la información necesaria para el registro del usuario.
     * @throws RoleNotFoundException Sí ocurre un error al buscar el rol "user" en la base de datos.
     * @return Retorna los datos del usuario registrado en un objeto UserAccountResponseDTO.
     */
    @Override
    @Transactional
    public UserAccountResponseDTO registerUser(UserAccountRequestDTO userAccountRequestDTO) throws MappingException {
        Role role = roleRepository.findRoleByKeyword("user");
        if (role == null) {
            throw new RoleNotFoundException(ROLE_NOT_FOUND_MSG);
        }
        userAccountRequestDTO.setPassword(passwordEncoder.encode(userAccountRequestDTO.getPassword()));
        User user = DTOEntityMapper.convertDTOToEntity(userAccountRequestDTO, User.class);
        user.setRoles(Arrays.asList(role));
        return DTOEntityMapper
                .convertEntityToDTO(userRepository.save(user), UserAccountResponseDTO.class);
    }
    /**
     * Actualiza el perfil del usuario.
     *
     * @param request Se utiliza para obtener el token a través de las cookies o los headers.
     * @throws PasswordUpdateNotAllowedException Si el usuario intenta actualizar la contraseña.
     * @return Retorna los datos del usuario actualizado en un objeto UserAccountResponseDTO.
     */

    @Override
    @Transactional
    public UserAccountResponseDTO updateProfile(HttpServletRequest request, UserAccountRequestDTO userAccountRequestDTO) throws MappingException {
        User user = getUserFromRequest(request);
        user.setName(userAccountRequestDTO.getName());
        user.setLastname(userAccountRequestDTO.getLastname());
        user.setEmail(userAccountRequestDTO.getEmail());
        if (userAccountRequestDTO.getPassword() != null) {
            throw new PasswordUpdateNotAllowedException(PASSWORD_UPDATE_NOT_ALLOWED_MSG
                    .concat(user.getId().toString()));
        }
        return DTOEntityMapper
                .convertEntityToDTO(userRepository.save(user), UserAccountResponseDTO.class);
    }
    /**
     * Actualiza la contraseña del usuario.
     *
     * @param request Se utiliza para obtener el token a través de las cookies o los headers.
     * @throws UpdateProfileNotAllowedException Si el usuario intenta actualizar otros datos además de la contraseña.
     * @return Retorna los datos del usuario actualizado en un objeto UserAccountResponseDTO.
     */
    @Override
    @Transactional
    public UserAccountResponseDTO updatePassword(HttpServletRequest request, UserAccountRequestDTO userAccountRequestDTO) throws MappingException {
        User user = getUserFromRequest(request);
        user.setPassword(passwordEncoder.encode(userAccountRequestDTO.getPassword()));
        if (userAccountRequestDTO.getName() != null ||
                userAccountRequestDTO.getLastname() != null ||
                userAccountRequestDTO.getEmail() != null) {
            throw new UpdateProfileNotAllowedException(UPDATE_PASSWORD_ERROR_MSG
                    .concat(user.getId().toString()));
        }
        return DTOEntityMapper
                .convertEntityToDTO(userRepository.save(user), UserAccountResponseDTO.class);
    }

    /**
     * Obtiene el perfil del usuario.
     *
     * @param request Se utiliza para obtener el token a través de las cookies o los headers.
     * @return Retorna los datos del usuario en un objeto UserAccountResponseDTO.
     */
    @Override
    @Transactional(readOnly = true)
    public UserAccountResponseDTO getProfile(HttpServletRequest request) throws MappingException {
        User user = getUserFromRequest(request);
        return DTOEntityMapper
                .convertEntityToDTO(user, UserAccountResponseDTO.class);
    }

    /**
     * Se envia un correo electronico(incluye un token unico) para restablecer la contraseña
     *
     * @param userAccountRequestDTO obtiene el email a través del dto.
     * @throws EmailNotFoundException Sí ocurre un error al obtener al usuario atraves de su correo(nulo o vacio)
     * @throws ExpiredJwtException Sí ocurre un error si el token esta expirado no se maneja
     * @throws SignatureException Sí ocurre un error si el token no es válido no se maneja
     * @throws MalformedJwtException Sí ocurre un error si el token está malformado no se maneja
     * @throws IllegalArgumentException Sí ocurre un error si el token solo cuenta con 2 digitos etc.
     * No se manejan las cuatro excepciones esto es porque si al enviar el token,
     * se pasa del tiempo de expiracion pueda a volver a generar un nuevo token.
     * @throws MessagingException Sí ocurre un error al enviar el correo.
     */
    @Override
    @Transactional
    public void forgotPassword(UserAccountRequestDTO userAccountRequestDTO) {
        User user = userRepository.getUserByEmail(userAccountRequestDTO.getEmail()).orElse(null);
        if (user == null) {
            throw new EmailNotFoundException(EMAIL_NOT_FOUND_MSG);
        }

        if (user.getToken() != null) {
            try {
                TokenJwtConfig.parseToken(user.getToken());
                throw new TokenNotExpiredException(TOKEN_NOT_EXPIRED_MSG);
            } catch (ExpiredJwtException ex) {
                // El token ha expirado, no es necesario hacer nada aquí
            } catch (SignatureException ep) {
                // El token no es válido, no es necesario hacer nada aquí
            } catch (MalformedJwtException e) {

            } catch (IllegalArgumentException e) {

            }
        }

        // Generar un nuevo token y asignarlo al usuario
        String token = generateNewToken(user);
        user.setToken(token);

        try {
            emailService.sendEmail(user.getEmail(),
                    TOKEN_EXPIRATION_EMAIL_SUBJECT
                            .concat(String.valueOf(TOKEN_EXPIRATION_TIME_FORGET_PASSWORD / 60000)
                                    .concat(TOKEN_EXPIRATION_EMAIL_BODY)),
                    SENDER_EMAIL, TemplateType.FORGET_PASSWORD_TEMPLATE, user);
        } catch (MessagingException e) {
            throw new EmailSendingException(ERROR_SENDING_EMAIL_MSG + e.getMessage());
        }
    }


    /**
     * Restaura la contraseña del usuario utilizando un token único.
     *
     * @param token El token único proporcionado para restaurar la contraseña.
     * @param userAccountRequestDTO Contiene los datos necesarios para actualizar la contraseña (nuevo password).
     * @throws EmailSendingException Si ocurre un error al enviar el correo electrónico de notificación.
     * @throws MalformedJwtException Si el token proporcionado es inválido o está malformado.
     */
    @Override
    public void restorePassword(String token, UserAccountRequestDTO userAccountRequestDTO) throws EmailSendingException {
        Claims claims = TokenJwtConfig.parseToken(token);
        String email = claims.getSubject();
        User user = userRepository.getUserByEmail(email).orElse(null);
        user.setPassword(passwordEncoder.encode(userAccountRequestDTO.getPassword()));
        if (user.getToken().equals(token)) {
            user.setToken("");
            userRepository.save(user);
        } else {
            throw new MalformedJwtException(MALFORMED_TOKEN_MSG);
        }
    }
    /**
     * Cierra la sesión del usuario.
     *
     * @param request Se utiliza para obtener el token a través de las cookies o los headers.
     * @param response Se usa para eliminar la cookie con nombre "jwt" y el encabezado "Authorization" de la respuesta.
     */
    @Override
    public void logoutUser(HttpServletRequest request, HttpServletResponse response) {
        // Eliminar la cookie con nombre "jwt"
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0); // Establecer tiempo de vida a cero para eliminar la cookie
                    response.addCookie(cookie);
                    break;
                }
            }
        }
        // Quitar el encabezado "Authorization" de la respuesta
        response.setHeader(TokenJwtConfig.HEADER_AUTHORIZATION, "");
    }
    /**
     * Genera un token para el usuario.
     *
     * @param user Se utiliza para obtener el email y generar el token.
     * @return Retorna el token generado.
     */
    private String generateNewToken(User user) {
        return TokenJwtConfig.generateToken(user.getEmail(), TOKEN_EXPIRATION_TIME_FORGET_PASSWORD);
    }

    /**
     * Obtiene el usuario a partir de la solicitud HTTP (token).
     *
     * @param request Se utiliza para obtener el token a través de las cookies o los headers.
     * @throws TokenNotFoundException Si no se encuentra el token en la solicitud.
     * @throws EmailByTokenNotFoundException Si no se encuentra el correo electrónico en el token.
     * @throws UserNotFoundException Si el usuario no existe en la base de datos.
     * @return Retorna el objeto User correspondiente al token de la solicitud.
     */
    private User getUserFromRequest(HttpServletRequest request) {
        String token = TokenJwtConfig.getTokenFromRequest(request);
        if (token == null || token.isEmpty()) {
            throw new TokenNotFoundException(ACCESS_RESOURCE_LOGIN_MSG);
        }
        Claims claims = TokenJwtConfig.parseToken(token);
        String email = claims.getSubject();
        if (email == null || email.isEmpty()) {
            throw new EmailByTokenNotFoundException(ACCESS_RESOURCE_LOGIN_MSG);
        }
        User user = userRepository.getUserByEmail(email).orElse(null);
        if (user == null) {
            throw new UserNotFoundException(ACCESS_RESOURCE_LOGIN_MSG);
        }
        return user;
    }

}
