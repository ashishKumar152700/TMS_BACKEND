package com.tms.transportationManagementSystem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint
{
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException
    {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(response.SC_UNAUTHORIZED)
                .message(authException.getMessage())
                .path(request.getRequestURI())
                .build();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        new ObjectMapper().writeValue(response.getOutputStream(), errorResponseDto);
    }
}
