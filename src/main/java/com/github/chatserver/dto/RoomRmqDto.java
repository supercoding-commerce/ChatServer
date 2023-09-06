package com.github.chatserver.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomRmqDto {
    private String customRoomId;
    private Long sellerId;
    private String shopName;
    private Long userId;
    private String userName;
    private Long productId;
}
