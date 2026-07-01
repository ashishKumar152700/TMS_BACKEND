package com.tms.transportationManagementSystem.advice;

import com.tms.transportationManagementSystem.dto.ResponseDTO.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUsernameNotFound(UsernameNotFoundException ex, HttpServletRequest request)
    {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponseDto.builder()
                                                                .timestamp(LocalDateTime.now())
                                                                .status(HttpStatus.NOT_FOUND.value())
                                                                .error("User Not Found")
                                                                .message(ex.getMessage())
                                                                .path(request.getRequestURI())
                                                                .build()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request)
    {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponseDto.builder()
                                                                    .timestamp(LocalDateTime.now())
                                                                    .status(HttpStatus.UNAUTHORIZED.value())
                                                                    .error("Unauthorized")
                                                                    .message(ex.getMessage())
                                                                    .path(request.getRequestURI())
                                                                    .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request)
    {

        String message = ex.getBindingResult().getFieldError().getDefaultMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponseDto.builder()
                                                                    .timestamp(LocalDateTime.now())
                                                                    .status(HttpStatus.BAD_REQUEST.value())
                                                                    .error("Validation Failed")
                                                                    .message(message)
                                                                    .path(request.getRequestURI())
                                                                    .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception ex, HttpServletRequest request)
    {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponseDto.builder()
                                                                            .timestamp(LocalDateTime.now())
                                                                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                                                            .error("Internal Server Error")
                                                                            .message(ex.getMessage())
                                                                            .path(request.getRequestURI())
                                                                            .build()
        );
    }
}
