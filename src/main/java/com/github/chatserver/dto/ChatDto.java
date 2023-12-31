package com.github.chatserver.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatDto {
    private String customRoomId;
    private String sender;//채팅을 보낸 사람
    private String content;// 메세지
    private MessageType type;

}