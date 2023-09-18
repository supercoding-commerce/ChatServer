package com.github.chatserver.controller;

import com.github.chatserver.dto.ChatDto;
import com.github.chatserver.dto.ChatRmqDto;
import com.github.chatserver.dto.EnterChatDto;
import com.github.chatserver.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;


import java.util.Objects;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatController {
    private final ChatRoomService chatRoomService;
    @MessageMapping("/chat.sendMessage/{sellerId}/{productId}/{userId}")
    @SendTo("/topic/{sellerId}/{productId}/{userId}")
    public ChatRmqDto sendMessage(
            @Payload ChatDto chatDto,
            @DestinationVariable Long sellerId,
            @DestinationVariable Long productId,
            @DestinationVariable Long userId
    ){
        //ChatDto newChat = chatRoomService.countMessageTag(chatDto);
        return chatRoomService.publishMessage(chatDto);

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
        headerAccessor.getSessionAttributes().put("userName", enterChatDto.getUserName());
        headerAccessor.getSessionAttributes().put("userId", userId);
        headerAccessor.getSessionAttributes().put("shopName", enterChatDto.getShopName());
        headerAccessor.getSessionAttributes().put("sellerId", sellerId);
        headerAccessor.getSessionAttributes().put("role", enterChatDto.getRole());
        headerAccessor.getSessionAttributes().put("productId", productId);
        String customRoomId = createCustomRoomId(sellerId, productId, userId);
        headerAccessor.getSessionAttributes().put("customRoomId", customRoomId);
        //applicationContext.getBean(ChatRoomService.class).setCustomRoomId(customRoomId);

        if(!customRoomId.equals(enterChatDto.getCustomRoomId()))throw new RuntimeException();


        if(Objects.equals(enterChatDto.getRole(), "user")){
            chatRoomService.userJoined(customRoomId, userId);
            chatRoomService.publishRoom(customRoomId,userId, enterChatDto.getUserName(), sellerId,  enterChatDto.getShopName(), productId );
            log.info("user entered: " + enterChatDto.getUserName() + enterChatDto.getType());
            return enterChatDto;
        }else if(Objects.equals(enterChatDto.getRole(), "seller")){
            chatRoomService.sellerJoined(customRoomId, sellerId);
            log.info("seller entered: " + enterChatDto.getShopName() + enterChatDto.getType());
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
