package com.github.chatserver.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnterChatDto {
    private String customRoomId;
    private String shopName;
    private String userName;
    private String role;
    private MessageType type;
}
