package com.tms.transportationManagementSystem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler
{

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException
    {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(response.SC_FORBIDDEN)
                .message(accessDeniedException.getMessage())
                .path(request.getRequestURI())
                .build();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        new ObjectMapper().writeValue(response.getOutputStream(), errorResponseDto);
    }
}
