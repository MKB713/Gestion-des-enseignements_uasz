package com.uasz.daos.choix.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDTO {

    private String message;
    private String status;
    private Object data;

    public static MessageResponseDTO success(String message, Object data) {
        return MessageResponseDTO.builder()
                .message(message)
                .status("SUCCESS")
                .data(data)
                .build();
    }

    public static MessageResponseDTO success(String message) {
        return MessageResponseDTO.builder()
                .message(message)
                .status("SUCCESS")
                .build();
    }
}