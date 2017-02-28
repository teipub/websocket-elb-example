package io.teipub.config;

import io.teipub.WebSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import redis.clients.jedis.JedisShardInfo;
import redis.embedded.RedisServer;

import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * Created by tei on 2016-11-28.
 * teipub.io
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private RedisServer redisServer;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/websocket")
                .withSockJS();
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new WebSocketHandler();
    }

    @Bean
    public RedisTemplate redisTemplate() {
        try {
            redisServer = new RedisServer(6379);
        } catch (IOException e) {
            e.printStackTrace();
        }
        redisServer.start();

        JedisShardInfo shardInfo = new JedisShardInfo("localhost", 6379);
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setShardInfo(shardInfo);

        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

    @PreDestroy
    public void destroy() {
        redisServer.stop();
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory jedisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory);
        container.addMessageListener(webSocketHandler(), new PatternTopic("*"));
        return container;
    }

}