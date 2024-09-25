package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.UserPoint;

import java.util.List;

public interface PointService {

    /**
     * 특정 유저의 포인트를 조회한다.
     * @param id 유저 ID
     * @return 포인트
     */
    UserPoint point(long id);

    /**
     * 특정 유저의 포인트 충전/이용 내역을 조회한다.
     *
     * @param id 유저 ID
     * @return 포인트 충전/이용 내역
     */
    List<PointHistory> history(long id);

    /**
     * 특정 유저의 포인트를 충전한다.
     *
     * @param id 유저 ID
     * @param amount 충전할 포인트
     * @return 충전 후 포인트
     */
    UserPoint charge(long id, long amount);

    /**
     * 특정 유저의 포인트를 사용한다.
     *
     * @param id 유저 ID
     * @param amount 사용할 포인트
     * @return 사용 후 포인트
     */
    UserPoint use(long id, long amount);
}
