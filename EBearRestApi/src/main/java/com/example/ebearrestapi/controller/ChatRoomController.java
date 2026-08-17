package com.example.ebearrestapi.controller;

import com.example.ebearrestapi.dto.request.ReadReqDto;
import com.example.ebearrestapi.dto.request.ChatMessageReqDto;
import com.example.ebearrestapi.dto.response.ChatListResDto;
import com.example.ebearrestapi.service.MessageRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/chat")
public class ChatRoomController {
    private final MessageRoomService messageRoomService;
    private final RestTemplate restTemplate;
    @Value("${ws.chat.api.server}")
    private String socketServerUrl;

    @PostMapping("/join")
    public ResponseEntity<?> joinMessageRoom(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.OK).body(messageRoomService.join(user));
    }

    @GetMapping("/user/me")
    public ResponseEntity<?> userMe(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.OK).body(messageRoomService.userMe(user));
    }

    @GetMapping("/rooms/admin")
    public ResponseEntity<?> chatRoomList() {
        return ResponseEntity.status(HttpStatus.OK).body(messageRoomService.getChatRoomList());
    }

    @PostMapping("/message")
    public ResponseEntity<?> sendMessage(@AuthenticationPrincipal User user, @RequestBody ChatMessageReqDto chatMessageReqDto) {
        return ResponseEntity.status(HttpStatus.OK).body(messageRoomService.saveChatMessage(chatMessageReqDto));
    }

    @GetMapping("/rooms/{id}/messages")
    public ResponseEntity<?> findMessage(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(messageRoomService.findMessage(id));
    }

    @PostMapping("/rooms/{roomId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long roomId, @RequestBody ReadReqDto dto) {
        ChatListResDto updateDto = messageRoomService.markMessagesAsRead(roomId, dto.getUserId());

        try {
            restTemplate.postForEntity(socketServerUrl, updateDto, Void.class);
        } catch (Exception e) {
            log.error("Socket 서버로 읽음 알림 전송 실패", e);
        }

        return ResponseEntity.status(HttpStatus.OK).body(true);
    }
}
