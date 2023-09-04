//package com.github.chatserver.dto;
//
//import com.github.chatserver.service.ChatService;
//import lombok.Builder;
//import lombok.Data;
//import org.springframework.web.socket.WebSocketSession;
//
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Data
//public class InquiryChatRoom {
//
//    private String roomId; // 채팅방 아이디
//    private String name; // 채팅방 이름
//    //private Set<WebSocketSession> sessions = new HashSet<>(); // 해당 채팅방에 연결된 세션들
//    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
//
//    @Builder
//    public InquiryChatRoom(String roomId, String name){
//        this.roomId = roomId;
//        this.name = name;
//    }
//
//    public void handleAction(WebSocketSession session, ChatDto message, ChatService chatService){
//        //message 에 담긴 타입을 확인한다.
//        //이때 message 에서 getType 으로 가져온 내용이
//        //chatDto 의 열거형인 MessageType 안에 있는 ENTER 과 동일한 값이라면
//        if(message.getType().equals(ChatDto.MessageType.ENTER)){
//            //sessions 에 넘어온 session 을 담고,
//            addSession(session);
//
//            //message 에는 입장하였다는 메시지를 띄워줍니다.
//            message.setMessage(message.getSender() + " 님이 입장하였습니다.");
//            sendMessage(message,chatService);
//        } else if (message.getType().equals(ChatDto.MessageType.TALK)) {
//            message.setMessage(message.getMessage());
//            sendMessage(message,chatService);
//        }
//    }
//    public <T> void sendMessage(T message, ChatService service){
//        sessions.values().parallelStream().forEach(session -> service.sendMessage(session, message));
//    }
//
//    public void addSession(WebSocketSession session) {
//        sessions.put(session.getId(), session);
//    }
//
//    public void removeSession(WebSocketSession session) {
//        sessions.remove(session.getId());
//    }
//
//}
