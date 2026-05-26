package com.demo.tmdt.common.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)

public class UserPrincipal {

    String userId;
    String email;
    String role;
    String sessionId;
}