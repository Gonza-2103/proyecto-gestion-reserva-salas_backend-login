package com.example.login.exception;

import com.example.login.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 Manejador global de excepciones para todo el backend de login:
 Intercepta automáticamente los errores lanzados en los Controllers y Services,
 convirtiéndolos en respuestas JSON limpias y uniformes (ErrorResponseDTO) para Retrofit.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     Captura errores de validación de los DTOs:
     Devuelve HTTP 400 junto con un mapa que indica exactamente
     qué campo falló y por qué
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> manejarValidaciones(MethodArgumentNotValidException ex) {

        //Mapa para guardar cada campo con su respectivo mensaje de error (ej: "correo" -> "Formato no válido")
        Map<String, String> erroresPorCampo = new HashMap<>();

        //Recorre todos los errores de validación detectados por Spring
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            erroresPorCampo.put(error.getField(), error.getDefaultMessage());
        }

        //Construye el DTO estándar de error con el detalle campo por campo
        ErrorResponseDTO respuesta = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Error de validación en los datos enviados",
                erroresPorCampo
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    /**
     Captura errores cuando el JSON enviado está mal formado o contiene un rol inválido:
     Devuelve HTTP 400.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> manejarJsonInvalido(HttpMessageNotReadableException ex) {
        ErrorResponseDTO respuesta = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "El formato del JSON es inválido o el rol enviado no corresponde (use RELATOR o COORDINADOR)"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    /**
     Captura CredencialesInvalidasException (correo/contraseña incorrectos o cuenta desactivada):
     Devuelve HTTP 401 (Unauthorized)
     */
    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponseDTO> manejarCredencialesInvalidas(CredencialesInvalidasException ex) {
        ErrorResponseDTO respuesta = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
    }

    /**
     Captura RolNoCoincidenteException (el usuario seleccionó un perfil que no coincide con su rol en BD):
     Devuelve HTTP 403 (Forbidden)
     */
    @ExceptionHandler(RolNoCoincidenteException.class)
    public ResponseEntity<ErrorResponseDTO> manejarRolNoCoincidente(RolNoCoincidenteException ex) {
        ErrorResponseDTO respuesta = new ErrorResponseDTO(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(respuesta);
    }

    /**
     Captura RecursoNoEncontradoException (cuando no existe el usuario consultado por ID o correo):
     Devuelve HTTP 404 (Not Found)
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        ErrorResponseDTO respuesta = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    /**
     Captura CorreoDuplicadoException (intento de registrar o actualizar con un correo ya existente):
     Devuelve HTTP 409 (Conflict)
     */
    @ExceptionHandler(CorreoDuplicadoException.class)
    public ResponseEntity<ErrorResponseDTO> manejarCorreoDuplicado(CorreoDuplicadoException ex) {
        ErrorResponseDTO respuesta = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
    }

    /**
     Red de seguridad para cualquier error inesperado no contemplado arriba:
     Evita exponer trazas técnicas internas y devuelve siempre un JSON controlado con HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> manejarErrorGeneral(Exception ex) {
        ErrorResponseDTO respuesta = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ocurrió un error interno en el servidor: " + ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
    }
}
