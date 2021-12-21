package com.tys.openquant.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
//    @Value("${spring.redis.host}")
//    private String host;
//
//    @Value("${spring.redis.port}")
//    private int port;
//
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory(){
//        return new LettuceConnectionFactory(host, port);
//    }
//
//    // 원하는 타입으로 Redis에 데이터를 넣기위한 RestTemplate 설정
//    @Bean
//    public RedisTemplate<?,?> redisTemplate(){
//        RedisTemplate<?,?> redisTemplate = new RedisTemplate<>();
//        // redis의 key값과 value값에 이상한 16진법값이 들어가지 않도록(default로 JdkSerializationRedisSerializer 바이트관련 값이 들어간다고 한다)
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//
//        redisTemplate.setConnectionFactory(redisConnectionFactory());
//        return redisTemplate;
//    }
}
