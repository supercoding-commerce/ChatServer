package com.github.chatserver.dto;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRmqDto {
    private String customRoomId;
    private String role;
    private Long sellerId;
    private String sender;
    private String content;
    private String createdAt;
    private MessageType type;

}
