package com.example.bankcards.exception;

import com.example.bankcards.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String BAD_REQUEST_MESSAGE = "BAD REQUEST";
    private static final String NOT_FOUND_MESSAGE = "NOT FOUND";
    private static final String INTERNAL_ERROR_MESSAGE = "Internal Server Error";

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFound(NotFoundException e, HttpServletRequest request) {
        return new ErrorResponse(
                request.getRequestURI(),
                formatStatus(HttpStatus.NOT_FOUND),
                NOT_FOUND_MESSAGE,
                e.getMessage() != null ? e.getMessage() : "Страница не найдена",
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class, IllegalArgumentException.class})
    public ErrorResponse handleBadRequest(Exception e, HttpServletRequest request) {
        return new ErrorResponse(
                request.getRequestURI(),
                formatStatus(HttpStatus.BAD_REQUEST),
                BAD_REQUEST_MESSAGE,
                e.getMessage() != null ? e.getMessage() : "Некорректный запрос",
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleAll(Exception e, HttpServletRequest request) {
        return new ErrorResponse(
                request.getRequestURI(),
                formatStatus(HttpStatus.INTERNAL_SERVER_ERROR),
                INTERNAL_ERROR_MESSAGE,
                e.getMessage() != null ? e.getMessage() : "Произошла непредвиденная ошибка",
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtAuthenticationException.class)
    public ErrorResponse handleJwtAuthentication(JwtAuthenticationException e, HttpServletRequest request) {
        return new ErrorResponse(
                request.getRequestURI(),
                formatStatus(HttpStatus.UNAUTHORIZED),
                "JWT AUTH ERROR",
                e.getMessage(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JWTValidException.class)
    public ErrorResponse handleJwtValidation(JWTValidException e, HttpServletRequest request) {
        return new ErrorResponse(
                request.getRequestURI(),
                formatStatus(HttpStatus.BAD_REQUEST),
                "JWT VALIDATION ERROR",
                e.getMessage(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidCredentialsException.class)
    public ErrorResponse handleInvalidCredentials(InvalidCredentialsException e, HttpServletRequest request) {
        return new ErrorResponse(
                request.getRequestURI(),
                formatStatus(HttpStatus.UNAUTHORIZED),
                "INVALID CREDENTIALS",
                e.getMessage(),
                LocalDateTime.now()
        );
    }

    private String formatStatus(HttpStatus status) {
        return status.value() + " " + status.getReasonPhrase().toUpperCase();
    }
}
