package com.sue.redisson;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * @author sue
 * @date 2020/8/22 12:51
 */


@Slf4j
public class RedissonLockTests {
    @Test
    public void testRedissonLock(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.182.151:6379");
        config.useSingleServer().setPassword("shushun");
        RedissonClient redissonClient = Redisson.create(config);

        RLock rLock = redissonClient.getLock("order");
        try{
            rLock.lock(30, TimeUnit.SECONDS);
            log.info("我获得了锁");
            Thread.sleep(10000);
        }catch (InterruptedException e){
                e.printStackTrace();
        }finally {
            log.info("我释放了锁");
            rLock.unlock();
        }
    }
}
