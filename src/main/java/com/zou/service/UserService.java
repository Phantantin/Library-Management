package com.zou.service;

import com.zou.modal.User;
import com.zou.payload.dto.UserDTO;

import java.util.List;

public interface UserService {

    public User getCurrentUser() throws Exception;

    public List<UserDTO> getAllUser();

    User findById(Long id) throws Exception;
}
