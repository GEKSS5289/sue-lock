package com.sue.distributelock.controller;

import com.sue.distributelock.lock.ZookeeperLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author sue
 * @date 2020/8/22 12:28
 */

@RestController
@Slf4j
public class ZookeeperController {
    public String zookeeperLock(){
        log.info("我进入了方法");
        try(ZookeeperLock zookeeperLock = new ZookeeperLock()){
            if(zookeeperLock.getLock("order")){
                log.info("我获得了锁");
                Thread.sleep(10000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }
}
