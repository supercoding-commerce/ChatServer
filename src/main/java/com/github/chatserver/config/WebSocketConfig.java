package com.github.chatserver.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
       registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
//                    // 구독 메시지인 경우, 주제를 동적으로 설정
//                    String destination = accessor.getDestination();
//                    if (destination != null && destination.startsWith("/topic/")) {
//                        String[] parts = destination.split("/");
//                        if (parts.length == 4) {
//                            // /inquire/shopName/userId 형식의 주제에서 정보를 추출
//                            String sellerId = parts[2];
//                            String userId = parts[3];
//                            // seller와 user 정보를 현재 세션에 저장
//                            accessor.getSessionAttributes().put("sellerId", sellerId);
//                            accessor.getSessionAttributes().put("userId", userId);
//                        }
//                    }
//                }
//                return message;
//            }
//        });
//    }

//    // WebSocketHandler 에 관한 생성자 추가
//    private final ChatHandler chatHandler;
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        // endpoint 설정 : /ws/chat
//        // 이를 통해서 ws://localhost:8080/ws/chat 으로 요청이 들어오면 websocket 통신을 진행합니다.
//        registry.addHandler(chatHandler, "ws/chat").setAllowedOrigins("*");
//    }
}
