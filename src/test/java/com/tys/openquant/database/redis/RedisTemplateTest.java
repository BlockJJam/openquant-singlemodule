package com.tys.openquant.database.redis;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.HashOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.SetOperations;
//import org.springframework.data.redis.core.ValueOperations;
//
//import javax.transaction.Transactional;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RedisTemplateTest {

    /*@Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Test
    @Transactional
    void testStrings(){
        // given
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = "stringKey";

        // when
        valueOperations.set(key, "hello");
        String value = valueOperations.get(key);

        // then
        assertThat(value).isEqualTo("hello");
        redisTemplate.delete(key);
    }

    @Test
    @Transactional
    void testSet(){
        // given
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        String key = "setKey";

        // when
        setOperations.add(key, "1","2","3","1","2");

        Set<String> members = setOperations.members(key);
        Long size = setOperations.size(key);

        //then
        assertThat(members).containsOnly("1","2","3");
        assertThat(size).isEqualTo(3);
        redisTemplate.delete(key);
    }

    @Test
    @Transactional
    void testHash(){
        // given
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        String key = "hashKey";

        // when
        hashOperations.put(key, "hello", "world");

        // then
        Object value = hashOperations.get(key, "hello");
        assertThat(value).isEqualTo("world");

        Map<Object, Object> entries = hashOperations.entries(key);
        assertThat(entries.keySet()).containsExactly("hello");
        assertThat(entries.values()).containsExactly("world");

        long size = hashOperations.size(key);
        assertThat(size).isEqualTo(entries.size());
        redisTemplate.delete(key);
    }

    @Test
    void testDelete(){
        // given
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = "stringKey";
        valueOperations.set(key, "hello");

        // when
        redisTemplate.delete(key);
        String value = valueOperations.get(key);

        // then
        assertThat(value).isEqualTo(null);
    }

    @Test
    void testExpire() throws InterruptedException {
        // given
        String token = "token1";
        String ip = "1.1.1.1";
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        // when
        hashOperations.put(token, "jwt", ip);
        redisTemplate.expire(token,10, TimeUnit.SECONDS);

        // then
        String retIp = hashOperations.get(token,"jwt");
        assertThat(retIp).isEqualTo(ip);

        TimeUnit.SECONDS.sleep(12);
        String delCheckedIp = hashOperations.get(token,"jwt");
        assertThat(delCheckedIp).isEqualTo(null);
    }*/
}
