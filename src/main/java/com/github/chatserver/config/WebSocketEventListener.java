package com.github.chatserver.config;

import com.github.chatserver.dto.EnterChatDto;
import com.github.chatserver.dto.MessageType;
import com.github.chatserver.service.ChatRoomService;
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
public class WebSocketEventListener {
    private final SimpMessageSendingOperations messageTemplate;
    private final ChatRoomService chatRoomService;

    @EventListener
    public void handleWebSocketDisconnectListener(
            SessionDisconnectEvent event
    ) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String shopName = (String) headerAccessor.getSessionAttributes().get("shopName");
        String userName = (String) headerAccessor.getSessionAttributes().get("userName");
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        Long sellerId = (Long) headerAccessor.getSessionAttributes().get("sellerId");
        Long productId = (Long) headerAccessor.getSessionAttributes().get("productId");
        String customRoomId = (String) headerAccessor.getSessionAttributes().get("customRoomId");
        String role = (String) headerAccessor.getSessionAttributes().get("role");
        System.out.println("role left room: " + role);
        switch(role){
            case "seller" :
                chatRoomService.sellerLeft(customRoomId, sellerId);
                log.info("Seller disconnected {}", shopName);
                var sellerMessage = EnterChatDto.builder()
                        .type(MessageType.LEAVE)
                        .shopName(shopName)
                        .role(role)
                        .build();
                messageTemplate.convertAndSend("/topic/" + sellerId + "/" + productId + "/" + userId, sellerMessage);

                break;

            default :
                chatRoomService.userLeft(customRoomId, userId);
                log.info("User disconnected {}", userName);
                var chatMessage = EnterChatDto.builder()
                        .type(MessageType.LEAVE)
                        .userName(userName)
                        .role(role)
                        .build();
                messageTemplate.convertAndSend("/topic/" + sellerId + "/" + productId + "/" + userId, chatMessage);
        }


        if (chatRoomService.isUserChatRoomEmpty(customRoomId) && chatRoomService.isSellerChatRoomEmpty(customRoomId)) {
            log.info("WS gonna be terminated {}", customRoomId);
            var chatMessage = EnterChatDto.builder()
                    .type(MessageType.TERMINATE)
                    .userName(null)
                    .shopName(null)
                    .build();
            messageTemplate.convertAndSend("/topic/" + sellerId + "/" + productId + "/" + userId, chatMessage);

        }
    }
}
