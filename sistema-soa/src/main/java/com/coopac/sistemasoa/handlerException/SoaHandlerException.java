package com.coopac.sistemasoa.handlerException;

import com.coopac.sistemasoa.exception.SoaException;
import com.coopac.sistemasoa.socio.model.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SoaHandlerException {

    @ExceptionHandler(SoaException.class)
    public ResponseEntity<ErrorResponse> handleSoaException(SoaException ex) {
        HttpStatus status = resolveHttpStatus(ex.getCode());
        return new ResponseEntity<>(buildErrorResponse(ex.getCode(), ex.getMesagge()), status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(
                buildErrorResponse("500", "Ocurrio un error inesperado: " + ex.getMessage()), // Agregamos ex.getMessage() para verlo en el alert del navegador
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private HttpStatus resolveHttpStatus(String code) {
        try {
            int statusCode = Integer.parseInt(code);
            return HttpStatus.valueOf(statusCode);
        } catch (IllegalArgumentException | NullPointerException e) {
            // Si el código no es numérico o no existe en HTTP, devolvemos 400 Bad Request
            return HttpStatus.BAD_REQUEST;
        }
    }

    private ErrorResponse buildErrorResponse(String code, String message) {
        ErrorResponse error = new ErrorResponse();
        error.setCode(code);
        error.setMesagge(message);
        return error;
    }
}
