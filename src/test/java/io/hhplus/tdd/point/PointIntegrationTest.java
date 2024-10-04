package io.hhplus.tdd.point;

import io.hhplus.tdd.common.Constants;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PointIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(PointIntegrationTest.class);

    @Autowired
    PointService pointService;

    @Test
    @DisplayName("포인트 동시성 테스트(포인트 충전 및 사용)")
    void pointConcurrentTest() throws InterruptedException {
        // given
        pointService.charge(1L, 3000L);

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(6);

        executorService.submit(() -> pointService.charge(1L, 1000L));
        executorService.submit(() -> pointService.charge(1L, 1000L));
        executorService.submit(() -> pointService.charge(1L, 1000L));
        executorService.submit(() -> pointService.use(1L, 500L));
        executorService.submit(() -> pointService.use(1L, 500L));
        executorService.submit(() -> pointService.use(1L, 500L));

        if (!executorService.awaitTermination(Constants.LOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
            executorService.shutdownNow();
        }

        // then
        UserPoint userPoint = pointService.point(1L);
        assertEquals(4500L, userPoint.point());
    }
}
