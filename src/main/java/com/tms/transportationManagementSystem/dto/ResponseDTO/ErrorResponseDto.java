package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponseDto
{
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
}
