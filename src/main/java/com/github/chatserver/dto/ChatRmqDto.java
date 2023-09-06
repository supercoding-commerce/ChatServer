package com.github.chatserver.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRmqDto {
    private String customRoomId;
    private Integer messageTag;
    private String sender;
    private String content;

}
