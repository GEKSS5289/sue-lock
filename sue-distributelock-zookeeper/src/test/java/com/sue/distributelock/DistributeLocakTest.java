package com.sue.distributelock;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMultiLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author sue
 * @date 2020/8/22 12:40
 */

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
public class DistributeLocakTest {

    @Test
    public void testCuratorLock(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.182.150:2181",retryPolicy);
        client.start();
        InterProcessMutex lock = new InterProcessMutex(client,"/order");
        try{
            if(!lock.acquire(30, TimeUnit.SECONDS)){
                try{
                    log.info("我获得了锁");
                }finally {
                    lock.release();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.close();
    }
}
