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
import org.hugo.backend.users.app.utils.TypeTemplate;
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
    EmailService emailService;
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


    @Override
    @Transactional(readOnly = true)
    public UserAccountResponseDTO getProfile(HttpServletRequest request) throws MappingException {
        User user = getUserFromRequest(request);
        return DTOEntityMapper
                .convertEntityToDTO(user, UserAccountResponseDTO.class);
    }

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
                    SENDER_EMAIL, TypeTemplate.FORGET_PASSWORD_TEMPLATE, user);
        } catch (MessagingException e) {
            throw new EmailSendingException(ERROR_SENDING_EMAIL_MSG + e.getMessage());
        }
    }

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

    private String generateNewToken(User user) {
        return TokenJwtConfig.generateToken(user.getEmail(), TOKEN_EXPIRATION_TIME_FORGET_PASSWORD);
    }


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
