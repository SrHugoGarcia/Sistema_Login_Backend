package org.hugo.backend.users.app.services;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hugo.backend.users.app.controllers.dto.UserAccountRequestDTO;
import org.hugo.backend.users.app.controllers.dto.UserAccountResponseDTO;
import org.hugo.backend.users.app.controllers.dto.UserResponseDTO;
import org.hugo.backend.users.app.exceptions.user.EmailSendingException;
import org.modelmapper.MappingException;

public interface UserAccountService {
    UserAccountResponseDTO registerUser(UserAccountRequestDTO user)throws MappingException;
    UserAccountResponseDTO updateProfile(HttpServletRequest request,UserAccountRequestDTO user)throws MappingException;
    UserAccountResponseDTO updatePassword(HttpServletRequest request,UserAccountRequestDTO user) throws MappingException;
    UserAccountResponseDTO getProfile(HttpServletRequest request)throws MappingException;
    void forgotPassword(UserAccountRequestDTO userAccountRequestDTO) throws EmailSendingException;
    void restorePassword(String token,UserAccountRequestDTO userAccountRequestDTO) throws EmailSendingException;
    void logoutUser(HttpServletRequest request, HttpServletResponse response);

}
