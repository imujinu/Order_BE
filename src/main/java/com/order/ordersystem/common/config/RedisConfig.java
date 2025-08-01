package com.order.ordersystem.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;


    @Bean
    // Qualifier : 같은 Bean객체가 여러 개 있을 경우  Bean 객체를 구분하기 위한 어노테이션
    @Qualifier("rtInventory")
    public RedisConnectionFactory redisConnectionFactory(){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setDatabase(0);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    @Qualifier("stockInventory")
    public RedisConnectionFactory stockConnectionFactory(){

        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setDatabase(1);
        return new LettuceConnectionFactory(configuration);
    }

    // Bean들끼리 서로 의존성을 주입받을 때 메서드 파라미터로도 가능
    //모든 template 중에 무조건 redisTemplate 이라는 메서드 명이 반드시 1개는 있어야함
    @Bean
    @Qualifier("rtInventory")
    public RedisTemplate<String, String> redisTemplate(@Qualifier("rtInventory" )RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // 객체로 value값을 써줄 수도 있다.
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    @Qualifier("stockInventory")
    public RedisTemplate<String, String> stockTemplate(@Qualifier("stockInventory" )RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // 객체로 value값을 써줄 수도 있다.
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}
