package com.demo.tmdt.mapper;

import com.demo.tmdt.dto.response.UserResponse;
import com.demo.tmdt.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getTen(),
                user.getSoDienThoai(),
                user.getRole()
        );
    }
}
