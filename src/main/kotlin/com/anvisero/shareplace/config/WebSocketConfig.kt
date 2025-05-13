package com.anvisero.shareplace.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws-chat").setAllowedOriginPatterns("*").withSockJS()
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        // Префикс для сообщений от клиента к серверу (в методы @MessageMapping)
        registry.setApplicationDestinationPrefixes("/app")
        // Префикс для топиков, на которые подписываются клиенты (сервер шлет сообщения сюда)
        // SimpleBroker - простой брокер в памяти. Для production рассмотрите RabbitMQ, ActiveMQ, Redis
        registry.enableSimpleBroker("/topic", "/queue")
        // Для приватных сообщений пользователю (например, уведомления)
        registry.setUserDestinationPrefix("/user")
    }
}