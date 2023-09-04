package com.github.chatserver.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class MessageDto {
    private Integer roomId;
    private Long productId;
    private String userName;
    private String shopName;
    private Map<String, Object> content;


    public void addToContent(String key, Object value) {
        if (content == null) {
            content = new HashMap<>();
        }
        content.put(key, value);
    }
}
