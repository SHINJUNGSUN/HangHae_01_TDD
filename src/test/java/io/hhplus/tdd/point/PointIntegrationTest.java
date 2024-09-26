package io.hhplus.tdd.point;

import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PointIntegrationTest {

    @Autowired
    PointService pointService;

    @Test
    @DisplayName("포인트 동시성 테스트(포인트 충전 및 사용)")
    void pointConcurrentTest() throws InterruptedException {
        // given
        pointService.charge(1L, 1000L);

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(10);


        for(int i = 0; i < 10; i++) {
            int index = i;
            executorService.submit(() -> {
                if (index % 2 == 0) {
                    pointService.charge(1L, 100);
                } else {
                    pointService.use(1L, 100);
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        // then
        UserPoint userPoint = pointService.point(1L);
        assertNotNull(userPoint);
        assertEquals(1000L, userPoint.point());
    }
}
