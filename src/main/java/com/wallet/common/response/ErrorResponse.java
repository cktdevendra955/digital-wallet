package com.wallet.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private boolean success;
    private String message;
    private int status;
    private LocalDateTime timestamp;

    public static ErrorResponse of(String message, int status) {
        return new ErrorResponse(false, message, status, LocalDateTime.now());
    }
}
