package com.sue.distributelock.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author sue
 * @date 2020/8/22 10:25
 */

@RestController
@Slf4j
public class RedisLockController {


    @Value("${shushun.name}")
    private String name;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("test")
    public String test(){
        return name;
    }

    @RequestMapping("redisLock")
    public String redisLock() {
        log.info("我进入了方法");
        String key = "redisKey";
        String value = UUID.randomUUID().toString();
        RedisCallback<Boolean> redisCallback = redisConnection -> {
            RedisStringCommands.SetOption setOption = RedisStringCommands.SetOption.ifAbsent();
            Expiration expiration = Expiration.seconds(30);
            byte[] redisKey = redisTemplate.getKeySerializer().serialize(key);
            byte[] redisValue = redisTemplate.getValueSerializer().serialize(value);
            Boolean set = redisConnection.set(redisKey, redisValue, expiration, setOption);
            return set;
        };

        Boolean execute = (Boolean)redisTemplate.execute(redisCallback);
        if(execute){
            log.info("我进入了锁");
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                RedisScript<Boolean> redisScript = RedisScript.of(script,Boolean.class);
                List keys = Arrays.asList(key);
                Boolean result = (Boolean)redisTemplate.execute(redisScript, keys, value);
                log.info("释放锁的结果:"+result);
            }
        }
        log.info("方法执行完成");
        return "方法执行完成";

    }
}