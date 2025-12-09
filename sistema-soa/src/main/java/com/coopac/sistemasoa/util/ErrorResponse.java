package com.coopac.sistemasoa.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorResponse {
    private String code;
    private String mesagge;

}
