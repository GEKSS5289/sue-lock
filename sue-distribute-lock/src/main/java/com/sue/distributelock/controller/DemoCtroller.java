package com.sue.distributelock.controller;

import com.sue.distributelock.entity.DistributeLock;
import com.sue.distributelock.mapper.DistributeLockMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author sue
 * @date 2020/8/22 9:28
 */

@RestController
@Slf4j
public class DemoCtroller {

    @Resource
    private DistributeLockMapper distributeLockMapper;

    @RequestMapping("singleLock")
    @Transactional(rollbackFor = Exception.class)
    public String singleLock() throws Exception {

        log.info("我进入了方法");
        DistributeLock distributeLock = distributeLockMapper.selectDistributeLock("demo");
        if(distributeLock == null){
            throw new Exception("分布式锁找不到");
        }
        log.info("我进入了锁");
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {

            }
        return "我已经执行完成";

    }
}
