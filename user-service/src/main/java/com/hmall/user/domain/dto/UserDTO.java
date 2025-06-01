package com.hmall.user.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable {
    private Long userId;
    private String username;
    private String phone;
    private String token;
}
