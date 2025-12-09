package com.coopac.sistemasoa.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class SoaException extends RuntimeException{
    private String code;
    private String mesagge;
}
