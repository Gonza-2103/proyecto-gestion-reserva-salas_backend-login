package com.example.login.exception;

/**
 Excepción personalizada que se lanza cuando no se encuentra un recurso en la base de datos:
 Se traduce a un código HTTP 404 (Not Found).
 */
public class RecursoNoEncontradoException extends RuntimeException {

    //Constructor que recibe el mensaje descriptivo del error para enviarlo al cliente
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}