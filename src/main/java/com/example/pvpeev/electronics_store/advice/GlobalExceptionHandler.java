package com.example.pvpeev.electronics_store.advice;

import com.example.pvpeev.electronics_store.advice.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(ResourceNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<Void> handleResourceConflict(ResourceConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<Void> handleFileUploadError(FileUploadException exception) {
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(FileDeleteException.class)
    public ResponseEntity<Void> handleFileDeleteException() {
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Void> handleBadRequest(BadRequestException exception) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        final Throwable cause = ex.getRootCause();

        if (cause instanceof SQLException sqlEx) {
            final String sqlState = sqlEx.getSQLState();

            // "23505" is the standard SQL state for Unique Violation (Postgres, H2, DB2)
            // For MySQL, the error code is 1062
            if ("23505".equals(sqlState) || sqlEx.getErrorCode() == 1062) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // "23503" is the standard SQL state for Foreign Key Violation
            if ("23503".equals(sqlState) || sqlEx.getErrorCode() == 1451) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        // Generic fallback for other integrity issues
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
