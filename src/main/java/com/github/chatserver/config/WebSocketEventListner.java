package com.github.chatserver.config;

import com.github.chatserver.dto.EnterChatDto;
import com.github.chatserver.dto.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListner {

    private final SimpMessageSendingOperations messageTemplate;

    @EventListener
    public void handleWebSocketDisconnectListner(
            SessionDisconnectEvent event
    ){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        Long sellerId = (Long) headerAccessor.getSessionAttributes().get("sellerId");
        if(username != null){
            log.info("User disconnected {}", username);
            var chatMessage = EnterChatDto.builder()
                    .type(MessageType.LEAVE)
                    .userName(username)
                    .build();
            messageTemplate.convertAndSend("/topic/"+sellerId+"/"+userId, chatMessage);
        }
    }
}
