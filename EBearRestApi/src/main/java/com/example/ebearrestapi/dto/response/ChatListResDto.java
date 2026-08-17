package com.example.ebearrestapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatListResDto {
    private String id;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Integer messageCount;
}
