package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PointServiceTest {

    @InjectMocks
    PointServiceImpl pointServiceImpl;

    @Mock
    UserPointRepository userPointRepository;

    @Mock
    PointHistoryRepository pointHistoryRepository;

    private UserPoint currentPoint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currentPoint = new UserPoint(1L,6000L, System.currentTimeMillis());
    }

    @Test
    @DisplayName("포인트 조회 성공")
    void pointSuccess() {
        // given
        long id = 1L;

        when(userPointRepository.selectById(id)).thenReturn(currentPoint);

        // when
        UserPoint result = pointServiceImpl.point(id);

        // then
        assertEquals(currentPoint, result);

        verify(userPointRepository).selectById(id);
    }

    @Test
    @DisplayName("포인트 충전/이용 내역 조회 성공")
    void historySuccess() {
        // given
        long id = 1L;
        List<PointHistory> pointHistoryList = List.of(new PointHistory(1L, id, 1000L, TransactionType.CHARGE, System.currentTimeMillis()));

        when(pointHistoryRepository.selectAllByUserId(id)).thenReturn(pointHistoryList);

        // when
        List<PointHistory> result = pointServiceImpl.history(id);

        // then
        assertIterableEquals(pointHistoryList, result);
    }

    @Test
    @DisplayName("충전할 포인트가 0보다 작거나 같은 경우, 실패")
    void chargeFailWhenAmountIsZeroOrNegative() {
        // given
        long id = 1L;
        long amount = -100L;

        // when & then
        assertThrows(IllegalArgumentException.class, () -> pointServiceImpl.charge(id, amount));
    }

    @Test
    @DisplayName("충전 후 포인트가 최대 잔고보다 큰 경우, 실패")
    void chargeFailWhenExceedsMaxBalance() {
        // given
        long id = 1L;
        long amount = 5000L;

        when(userPointRepository.selectById(id)).thenReturn(currentPoint);

        // when & then
        assertThrows(IllegalStateException.class, () -> pointServiceImpl.charge(id, amount));
    }

    @Test
    @DisplayName("포인트 충전 성공")
    void chargeSuccess() {
        // given
        long id = 1L;
        long amount = 1000L;

        when(userPointRepository.selectById(id)).thenReturn(currentPoint);

        when(userPointRepository.insertOrUpdate(id, currentPoint.point() + amount)).thenReturn(new UserPoint(id, currentPoint.point() + amount, 0L));

        // when
        UserPoint result = pointServiceImpl.charge(id, amount);

        // then
        assertEquals(7000L, result.point());

        verify(userPointRepository).selectById(id);
        verify(userPointRepository).insertOrUpdate(id, currentPoint.point() + amount);
    }

    @Test
    @DisplayName("사용할 포인트가 0보다 작거나 같은 경우, 실패")
    void useFailWhenAmountIsZeroOrNegative() {
        // given
        long id = 1L;
        long amount = -100L;

        // when & then
        assertThrows(IllegalArgumentException.class, () -> pointServiceImpl.use(id, amount));
    }

    @Test
    @DisplayName("사용 후 포인트가 0보다 작은 경우, 실패")
    void useFailWhenBalanceGoesNegative() {
        // given
        long id = 1L;
        long amount = 7000L;

        when(userPointRepository.selectById(id)).thenReturn(currentPoint);

        // when & then
        assertThrows(IllegalStateException.class, () -> pointServiceImpl.use(id, amount));
    }

    @Test
    @DisplayName("포인트 사용 성공")
    void useSuccess() {
        // given
        long id = 1L;
        long amount = 1000L;

        when(userPointRepository.selectById(id)).thenReturn(currentPoint);

        when(userPointRepository.insertOrUpdate(id, currentPoint.point() - amount)).thenReturn(new UserPoint(id, currentPoint.point() - amount, 0L));

        // when
        UserPoint result = pointServiceImpl.use(id, amount);

        // then
        assertEquals(5000L, result.point());

        verify(userPointRepository).selectById(id);
        verify(userPointRepository).insertOrUpdate(id, currentPoint.point() - amount);
    }
}
