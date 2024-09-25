package io.hhplus.tdd.point.service;

import io.hhplus.tdd.common.Constants;
import io.hhplus.tdd.point.controller.PointController;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    /**
     * 특정 유저의 포인트를 조회한다.
     *
     * @param id 유저 ID
     * @return 포인트
     */
    @Override
    public UserPoint point(long id) {
        return userPointRepository.selectById(id);
    }

    /**
     * 특정 유저의 포인트 충전/이용 내역을 조회한다.
     *
     * @param id 유저 ID
     * @return 포인트 충전/이용 내역
     */
    @Override
    public List<PointHistory> history(long id) {
        return pointHistoryRepository.selectAllByUserId(id);
    }

    /**
     * 특정 유저의 포인트를 충전한다.
     *
     * @param id 유저 ID
     * @param amount 충전할 포인트
     * @return 충전 후 포인트
     */
    @Override
    public UserPoint charge(long id, long amount) {

        /*
         * 행동 분석
         * 1. 현재 포인트를 조회한다.
         * 2. 포인트를 충전한다.
         * 3. 포인트 충전 내역을 저장한다.
         * 4. 포인트 충전 결과를 반환한다.
         *
         * Test Case
         * [실패]
         * 1. 충전할 포인트가 0보다 작거나 같은 경우, 실패한다.
         * 2. 충전 후 포인트가 최대 잔고보다 큰 경우, 실패한다.
         * [성공]
         */

        if(amount <= 0) {
            throw new IllegalArgumentException("충전할 포인트는 0보다 커야 합니다.");
        }

        UserPoint currentPoint = userPointRepository.selectById(id);

        if(currentPoint.point() + amount > Constants.MAX_POINT) {
            throw new IllegalStateException("충전 후 포인트가 최대 잔고보다 큽니다.");
        }

        UserPoint userPoint = userPointRepository.insertOrUpdate(id, currentPoint.point() + amount);

        try {
            pointHistoryRepository.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage(), e);
        }

        return userPoint;
    }

    /**
     * 특정 유저의 포인트를 사용한다.
     *
     * @param id 유저 ID
     * @param amount 사용할 포인트
     * @return 사용 후 포인트
     */
    @Override
    public UserPoint use(long id, long amount) {

        /*
         * 행동 분석
         * 1. 현재 포인트를 조회한다.
         * 2. 포인트를 사용한다.
         * 3. 포인트 사용 내역을 저장한다.
         * 4. 포인트 사용 결과를 반환한다.
         *
         * Test Case
         * [실패]
         * 1. 사용할 포인트가 0보다 작거나 같은 경우, 실패한다.
         * 2. 사용 후 포인트가 0보다 작은 경우, 실패한다.
         * [성공]
         */

        if(amount <= 0) {
            throw new IllegalArgumentException("사용할 포인트는 0보다 커야 합니다.");
        }

        UserPoint currentPoint = userPointRepository.selectById(id);

        if(currentPoint.point() - amount < 0) {
            throw new IllegalStateException("사용 후 포인트가 0보다 작습니다.");
        }

        UserPoint userPoint = userPointRepository.insertOrUpdate(id, currentPoint.point() - amount);

        try {
            pointHistoryRepository.insert(id, amount, TransactionType.USE, System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage(), e);
        }

        return userPoint;
    }
}
