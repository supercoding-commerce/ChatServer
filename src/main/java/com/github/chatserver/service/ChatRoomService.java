package com.github.chatserver.service;

import com.github.chatserver.dto.ChatDto;
import com.github.chatserver.dto.ChatRmqDto;
import com.github.chatserver.dto.RoomRmqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final Map<String, Set<Long>> userChatRooms = new ConcurrentHashMap<>();
    private final Map<String, Set<Long>> sellerChatRooms = new ConcurrentHashMap<>();
    private final Map<String, Integer> messageTagCounts = new ConcurrentHashMap<>();
    private final RabbitTemplate rabbitTemplate;

    // 사용자가 채팅방에 입장할 때 호출
    public void userJoined(String customRoomId, Long userId) {
        userChatRooms.computeIfAbsent(customRoomId, k -> new HashSet<>()).add(userId);
    }


    // 판매자가 채팅방에 입장할 때 호출
    public void sellerJoined(String customRoomId, Long sellerId) {
        sellerChatRooms.computeIfAbsent(customRoomId, k -> new HashSet<>()).add(sellerId);
    }


    // 사용자가 채팅방에서 퇴장할 때 호출
    public void userLeft(String customRoomId, Long userId) {
        userChatRooms.computeIfPresent(customRoomId, (k, v) -> {
            v.remove(userId);
            return v.isEmpty() ? null : v;
        });
    }

    // 판매자가 채팅방에서 퇴장할 때 호출
    public void sellerLeft(String customRoomId, Long sellerId) {
        sellerChatRooms.computeIfPresent(customRoomId, (k, v) -> {
            v.remove(sellerId);
            return v.isEmpty() ? null : v;
        });
    }

    // 채팅방에 사용자가 있는지 확인
    public boolean isUserChatRoomEmpty(String customRoomId) {
        return !userChatRooms.containsKey(customRoomId) || userChatRooms.get(customRoomId).isEmpty();
    }

    // 채팅방에 판매자가 있는지 확인
    public boolean isSellerChatRoomEmpty(String customRoomId) {
        return !sellerChatRooms.containsKey(customRoomId) || sellerChatRooms.get(customRoomId).isEmpty();
    }

    public void publishRoom(String customRoomId, Long userId, String userName, Long sellerId, String shopName, Long productId) {
        RoomRmqDto newRoom = RoomRmqDto.builder()
                .customRoomId(customRoomId)
                .userId(userId)
                .userName(userName)
                .sellerId(sellerId)
                .shopName(shopName)
                .productId(productId)
                .build();

//        Message message = MessageBuilder
//                .withBody(newChatRoom)
//                .setHeader("customRoomId", customRoomId) // customRoomId를 헤더에 추가
//                .build();

        // RabbitMQ로 메시지 전송
        //rabbitTemplate.send(RabbitMQConfig.EXCHANGE_NAME, "postChat." + customRoomId, message);
        rabbitTemplate.convertAndSend("exchange", "postRoom", newRoom);

    }

    public void publishMessage(ChatDto chatDto) {
        ChatRmqDto newChat = ChatRmqDto.builder()
                .customRoomId(chatDto.getCustomRoomId())
                .messageTag(chatDto.getMessageTag())
                .sender(chatDto.getSender())
                .content(chatDto.getContent())
                .build();

        int messageTag = chatDto.getMessageTag();
        String routingKey = "postChat" + (messageTag % 5 + 1);
        rabbitTemplate.convertAndSend("exchange", routingKey, newChat);
    }

    public ChatDto countMessageTag( ChatDto chatDto) {
        String customRoomId = chatDto.getCustomRoomId();

        Integer currentMessageTag =  messageTagCounts.get(customRoomId);

        if (currentMessageTag == null) {
            currentMessageTag = 1;
        } else {
            currentMessageTag++;
        }
        // messageTagCounts 맵에 현재 messageTag 값을 저장
        messageTagCounts.put(customRoomId, currentMessageTag);

        // chatDto에 업데이트된 messageTag 값을 설정
        chatDto.setMessageTag(currentMessageTag);

        // chatDto를 반환
        return chatDto;

    }


}

