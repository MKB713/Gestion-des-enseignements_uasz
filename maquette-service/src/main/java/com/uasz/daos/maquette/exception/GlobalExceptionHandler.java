package com.uasz.daos.maquette.exception;

import com.uasz.daos.maquette.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        return new ResponseEntity<>(ApiResponse.error(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflictException(ConflictException e) {
        return new ResponseEntity<>(ApiResponse.error(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        return new ResponseEntity<>(ApiResponse.error("Accès refusé : vous n'avez pas les droits nécessaires"),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            org.springframework.http.converter.HttpMessageNotReadableException e) {
        String detail = e.getMostSpecificCause().getMessage();
        return new ResponseEntity<>(ApiResponse.error("Format JSON invalide ou corps de requête manquant : " + detail),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception e) {
        return new ResponseEntity<>(ApiResponse.error("Une erreur interne est survenue : " + e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
