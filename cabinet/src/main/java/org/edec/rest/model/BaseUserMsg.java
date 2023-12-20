package org.edec.rest.model;

import lombok.Data;

@Data
public class BaseUserMsg {
    private String userToken;
    private Long idHumanface;
}
