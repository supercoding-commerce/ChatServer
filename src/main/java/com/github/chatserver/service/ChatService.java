//package com.github.chatserver.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.github.chatserver.dto.BroadCastChatRoom;
//import com.github.chatserver.dto.InquiryChatRoom;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//import java.util.*;
//
//@Slf4j
//@Data
//@Service
//public class ChatService {
//    private final ObjectMapper mapper;
//    private Map<String, InquiryChatRoom> chatRooms;
//
//    @PostConstruct
//    private void init(){
//        chatRooms = new LinkedHashMap<>();
//    }
//
//    public List<InquiryChatRoom> findAllRoom(){
//        return new ArrayList<>(chatRooms.values());
//    }
//
//    public InquiryChatRoom findRoomById(String roomId){
//        return chatRooms.get(roomId);
//    }
//
//    public InquiryChatRoom createRoom(String name){
//        String roomId = UUID.randomUUID().toString();
//
//        //Builder를 사용하여 ChatRoom 을 Build
//        InquiryChatRoom room = InquiryChatRoom.builder()
//                .roomId(roomId)
//                .name(name)
//                .build();
//        chatRooms.put(roomId,room);//랜덤 아이디와 room 정보를 Map 에 저장
//
//        return room;
//    }
//
//    public <T> void sendMessage(WebSocketSession session, T message){
//        try{
//            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));;
//        }catch (IOException e){
//            log.error(e.getMessage(),e);
//        }
//    }
//
//
//
//}