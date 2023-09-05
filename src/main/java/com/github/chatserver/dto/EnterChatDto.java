package com.github.chatserver.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnterChatDto {
    private String shopName;
    private String userName;
    private String chatName;
    private MessageType type;
}
