package com.app.playlist.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Jwt {
    String iss;
    String sub; //userID:username
    String exp;

    String jti;
    String role;

}
