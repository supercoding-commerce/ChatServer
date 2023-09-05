package com.github.chatserver.controller;

import com.github.chatserver.dto.ChatDto;
import com.github.chatserver.dto.EnterChatDto;
import com.github.chatserver.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatController {
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat.sendMessage/{sellerId}/{productId}/{userId}")
    @SendTo("/topic/{sellerId}/{productId}/{userId}")
    public ChatDto sendMessage(
            @Payload ChatDto chatDto,
            @DestinationVariable Long sellerId,
            @DestinationVariable Long productId,
            @DestinationVariable Long userId
    ){
            return chatDto;
    }

    @MessageMapping("/chat.addUser/{sellerId}/{productId}/{userId}")
    @SendTo("/topic/{sellerId}/{productId}/{userId}")
    public EnterChatDto addUser(
            @Payload EnterChatDto enterChatDto,
            @DestinationVariable Long sellerId,
            @DestinationVariable Long productId,
            @DestinationVariable Long userId,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        // 웹소켓 세션에 유저이름 넣기
        headerAccessor.getSessionAttributes().put("userName", enterChatDto.getUserName());
        headerAccessor.getSessionAttributes().put("userId", userId);
        headerAccessor.getSessionAttributes().put("shopName", enterChatDto.getShopName());
        headerAccessor.getSessionAttributes().put("sellerId", sellerId);
        headerAccessor.getSessionAttributes().put("chatName", enterChatDto.getChatName());
        headerAccessor.getSessionAttributes().put("productId", productId);
        String customRoomId = createCustomRoomId(sellerId, productId, userId);
        headerAccessor.getSessionAttributes().put("customRoomId", customRoomId);
        System.out.println(customRoomId);

        if(Objects.equals(enterChatDto.getChatName(), enterChatDto.getUserName())){
            chatRoomService.userJoined(customRoomId, userId);
            System.out.println("22222222222" + enterChatDto.getUserName() + enterChatDto.getType());
            return enterChatDto;
        }else if(Objects.equals(enterChatDto.getChatName(), enterChatDto.getShopName())){
            chatRoomService.sellerJoined(customRoomId, sellerId);
            System.out.println("22222222222" + enterChatDto.getShopName() + enterChatDto.getType());
            return enterChatDto;
        }

        return enterChatDto;

    }

    public static String createCustomRoomId(Long sellerId, Long productId, Long userId) {
        // userId, sellerId, productId를 6자리 문자열로 변환
        String userIdStr = String.format("%06d", userId);
        String sellerIdStr = String.format("%06d", sellerId);
        String productIdStr = String.format("%06d", productId);

        // customRoomId를 조합
        String customRoomId = sellerIdStr + productIdStr + userIdStr;

        return customRoomId;
    }
}
