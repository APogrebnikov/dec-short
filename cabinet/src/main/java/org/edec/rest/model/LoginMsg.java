package org.edec.rest.model;

import lombok.Data;

@Data
public class LoginMsg {
    private String username;
    private String password;
    private String appToken;
}
