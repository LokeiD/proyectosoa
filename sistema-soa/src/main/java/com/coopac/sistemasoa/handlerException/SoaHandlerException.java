package com.coopac.sistemasoa.handlerException;

import com.coopac.sistemasoa.exception.SoaException;
import com.coopac.sistemasoa.util.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SoaHandlerException {
    @ResponseStatus (code=HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SoaException.class)
    public Object genericBadRequestException(final SoaException ex){
        return new ErrorResponse(ex.getCode(),ex.getMesagge());
    }

    @ResponseStatus (code=HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Object genericException(final Exception ex){
        return new ErrorResponse("500","Ocurrio un error inesperado");
    }

}
