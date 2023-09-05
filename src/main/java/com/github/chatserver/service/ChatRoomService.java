package com.github.chatserver.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatRoomService {
    private final Map<String, Set<Long>> userChatRooms = new ConcurrentHashMap<>();
    private final Map<String, Set<Long>> sellerChatRooms = new ConcurrentHashMap<>();

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
}

