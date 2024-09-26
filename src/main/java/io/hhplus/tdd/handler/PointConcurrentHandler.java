package io.hhplus.tdd.handler;

import io.hhplus.tdd.common.Constants;
import io.hhplus.tdd.point.UserPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

@Component
public class PointConcurrentHandler {

    private static final Logger log = LoggerFactory.getLogger(PointConcurrentHandler.class);

    private final ConcurrentHashMap<Long, Lock> userLockMap = new ConcurrentHashMap<>();

    /**
     * 포인트 충전 및 사용에 동시성 처리를 한다.
     *
     * @param id 유저 ID
     * @param function 포인트 충전 및 사용
     * @return
     */
    public UserPoint executeConcurrentUserPoint(long id, Supplier<UserPoint> function) {

        Lock lock = userLockMap.computeIfAbsent(id, key -> new ReentrantLock());

        boolean acquired = false;

        try {
            acquired = lock.tryLock(Constants.LOCK_TIMEOUT, TimeUnit.MILLISECONDS);
            if(!acquired) {
                log.info("사용자 락 획득 타임아웃");
                throw new IllegalArgumentException("사용자 락 획득 타임아웃");
            }

            log.info("사용자 락 획득 성공, KEY : {}", id);

            return function.get();

        } catch (InterruptedException e) {
            throw new IllegalArgumentException("사용자 락 획득 오류");
        } finally {
            if(acquired) {
                lock.unlock();
                log.info("사용자 락 언락, KEY : {}", id);
            }
        }
    }
}
