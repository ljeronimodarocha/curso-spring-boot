package curso.spring.controller;

import curso.spring.exception.ExceptionDetails;
import curso.spring.exception.RegraNegocioException;
import curso.spring.exception.UsuarioOuSenhaInvalidaException;
import curso.spring.exception.ValidationExceptionDetails;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ApplicationControllerAdvice {

    @ExceptionHandler(RegraNegocioException.class)
    @ResponseStatus(BAD_REQUEST)
    public ExceptionDetails handleRegraNegocioException(RegraNegocioException ex) {
        return ExceptionDetails.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .title(ex.getMessage())
                .details(ex.getMessage())
                .developerMessage(ex.getClass().getName())
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ExceptionDetails handleEntityNotFoundException(EntityNotFoundException ex) {
        return ExceptionDetails.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .title("Entity not found")
                .details(ex.getMessage())
                .developerMessage(ex.getClass().getName())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ValidationExceptionDetails handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        String fields = fieldErrors.stream().map(FieldError::getField)
                .collect(Collectors.joining(", "));
        String fieldsMessage = fieldErrors.stream().map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ValidationExceptionDetails.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .title("Bad Request Exception, Invalid Fields")
                .details("Check the field(s) error")
                .developerMessage(ex.getClass().getName())
                .fields(fields)
                .fieldsMessage(fieldsMessage)
                .build();
    }

    @ExceptionHandler(UsuarioOuSenhaInvalidaException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ExceptionDetails handleMethodUsuarioOuSenhaInvalidaException(UsuarioOuSenhaInvalidaException ex) {
        return ExceptionDetails.builder()
                .details(ex.getMessage())
                .developerMessage(ex.getClass()
                        .getName())
                .title("UNAUTHORIZED")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ExceptionDetails handleMethodConstraintViolationException(ConstraintViolationException ex) {
        return ExceptionDetails.builder()
                .details(ex.getMessage())
                .developerMessage(ex.getClass()
                        .getName())
                .title("Registro ja cadastrado")
                .timestamp(LocalDateTime.now())
                .build();
    }


}
