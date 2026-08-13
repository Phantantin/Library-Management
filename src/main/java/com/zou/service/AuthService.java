package com.zou.service;

import com.zou.domain.AuthProvider;
import com.zou.exception.UserException;
import com.zou.payload.dto.UserDTO;
import com.zou.payload.response.AuthResponse;

public interface AuthService {

    AuthResponse login(String userName, String password) throws UserException;
    AuthResponse signup(UserDTO req) throws UserException;

    void createPasswordResetToken(String email) throws UserException;
    void resetPassword(String token, String newPassword) throws Exception;
}
