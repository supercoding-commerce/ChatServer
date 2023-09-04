package com.github.chatserver.controller;

import com.github.chatserver.dto.ChatDto;
import com.github.chatserver.dto.EnterChatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
//@RequestMapping("/chat")
public class ChatController {

//    private final ChatService service;
//
//    @PostMapping
//    public InquiryChatRoom createRoom(@RequestParam String name){
//
//        return service.createRoom(name);
//    }
//
//    @GetMapping
//    public List<InquiryChatRoom> findAllRooms(){
//        return service.findAllRoom();
//    }

    @MessageMapping("/chat.sendMessage/{sellerId}/{userId}")
    @SendTo("/topic/{sellerId}/{userId}")
    public ChatDto sendMessage(
            @Payload ChatDto chatDto,
            @DestinationVariable Long sellerId,
            @DestinationVariable Long userId
    ){
            return chatDto;
    }

    @MessageMapping("/chat.addUser/{sellerId}/{userId}")
    @SendTo("/topic/{sellerId}/{userId}")
    public EnterChatDto addUser(
            @Payload EnterChatDto enterChatDto,
            @DestinationVariable Long sellerId,
            @DestinationVariable Long userId,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        // 웹소켓 세션에 유저이름 넣기
        headerAccessor.getSessionAttributes().put("username", enterChatDto.getUserName());
        headerAccessor.getSessionAttributes().put("userId", userId);
        headerAccessor.getSessionAttributes().put("shopname", enterChatDto.getShopName());
        headerAccessor.getSessionAttributes().put("sellerId", sellerId);
        return enterChatDto;
    }

}
