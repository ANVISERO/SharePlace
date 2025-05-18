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

//    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
//        // Префикс для сообщений от клиента к серверу (в методы @MessageMapping)
//        registry.setApplicationDestinationPrefixes("/app")
//        // Префикс для топиков, на которые подписываются клиенты (сервер шлет сообщения сюда)
//        // SimpleBroker - простой брокер в памяти. Для production рассмотрите RabbitMQ, ActiveMQ, Redis
//        registry.enableSimpleBroker("/topic", "/queue")
//        // Для приватных сообщений пользователю (например, уведомления)
//        registry.setUserDestinationPrefix("/user")
//    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        // Префикс для сообщений от клиента к серверу (для методов @MessageMapping)
        registry.setApplicationDestinationPrefixes("/app")

        // Настройка STOMP-реле для подключения к RabbitMQ
        // Префиксы "/topic" и "/queue" будут обрабатываться внешним брокером
        registry.enableStompBrokerRelay("/topic", "/queue")
            .setRelayHost("localhost")       // Хост, где запущен RabbitMQ
            .setRelayPort(61613)           // Стандартный порт STOMP для RabbitMQ
            .setClientLogin("guest")       // Логин для подключения клиентов к RabbitMQ (через реле)
            .setClientPasscode("guest")    // Пароль для подключения клиентов к RabbitMQ (через реле)
            .setSystemLogin("guest")       // Логин для "системного" подключения Spring к RabbitMQ
            .setSystemPasscode("guest")    // Пароль для "системного" подключения
        // .setVirtualHost("/")      // Опционально: если вы используете специфичный virtual host в RabbitMQ
        // .setSystemHeartbeatSendInterval(10000) // Пример настройки интервала отправки heartbeats
        // .setSystemHeartbeatReceiveInterval(10000) // Пример настройки интервала ожидания heartbeats

        // Префикс для пользовательских очередей (для приватных сообщений)
        registry.setUserDestinationPrefix("/user")
    }
}