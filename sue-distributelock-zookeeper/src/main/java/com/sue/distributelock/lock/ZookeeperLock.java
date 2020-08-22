package com.sue.distributelock.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author sue
 * @date 2020/8/22 11:13
 */

@Slf4j
public class ZookeeperLock implements AutoCloseable, Watcher {

    private ZooKeeper zookeeper;
    private String znode;
    public ZookeeperLock() throws IOException {
        this.zookeeper = new ZooKeeper("192.168.182.150:2181",10000,this);
    }

    public void close() throws Exception {
        zookeeper.delete(znode,-1);
        zookeeper.close();
        log.info("我已经释放了锁");
    }

    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getType()== Event.EventType.NodeDeleted){
            synchronized (this){
                notify();
            }
        }
    }


    public boolean getLock(String businessCode){
        try {

            Stat exists = zookeeper.exists("/" + businessCode, false);
            if(exists == null){
                zookeeper.create(
                        "/"+businessCode,
                        businessCode.getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);
            }

           znode = zookeeper.create(
                    "/"+businessCode+"/"+businessCode+"_",
                    businessCode.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);

            List<String> children = zookeeper.getChildren("/" + businessCode, false);
            Collections.sort(children);
            String firstNode = children.get(0);
            if(znode.endsWith(firstNode)){
                return true;
            }
            //不是第一个子节点，则监听前一个节点
            String lastNode = firstNode;
            for(String node:children){
                if(znode.endsWith(node)){
                    zookeeper.exists("/"+businessCode+"/"+lastNode,true);
                    break;
                }else{
                    lastNode = node;
                }
            }

            synchronized (this){
                wait();
            }

            return true;
        } catch (KeeperException e) {

            e.printStackTrace();

        } catch (InterruptedException e) {

            e.printStackTrace();

        }
        return false;
    }
}
