package com.mak.sd.urlshortner;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Scope("singleton")
public class ZooKeeperTokenManager implements ApplicationListener<ContextRefreshedEvent> {

    private static final Long tokenBatchSize = 10L;
    private static final String ZNODE_TOKEN_INDEX = "/tokenIndex";

    @Autowired
    private CuratorFramework curator;

    private AtomicLong currentToken = new AtomicLong(0);
    private Long tokenEnd;

    public Long getCurrentToken() {
        if (currentToken.get() >= tokenEnd) {
            getNextTokenIndex();
        }
        return currentToken.incrementAndGet();
    }

    public synchronized void getNextTokenIndex() {
        InterProcessMutex lock = null;
        try {
            Stat stat = curator.checkExists().forPath(ZNODE_TOKEN_INDEX);
            if (stat == null) {
                curator.create().withMode(CreateMode.PERSISTENT).forPath(ZNODE_TOKEN_INDEX, "0".getBytes());
            }

            lock = new InterProcessMutex(curator, ZNODE_TOKEN_INDEX);

            if (lock.acquire(10, TimeUnit.SECONDS)) {
                byte[] b = curator.getData().forPath(ZNODE_TOKEN_INDEX);
                Integer tokenIndex = Integer.parseInt(new String(b)) ;
                curator.setData().forPath(ZNODE_TOKEN_INDEX, ((tokenIndex + 1) + "").getBytes());
                currentToken.set(tokenIndex * tokenBatchSize);
                tokenEnd = tokenIndex * tokenBatchSize + tokenBatchSize;
                System.out.println("NExt token index is :" + tokenIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Couldn't get token");
            System.exit(-1);
        } finally {
            try {
                if (lock != null)
                    lock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        getNextTokenIndex();
        System.out.println("Token during startup" + currentToken.get());
    }
}
