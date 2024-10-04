package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.UserPoint;

public interface UserPointRepository {
    UserPoint selectById(long id);
    UserPoint insertOrUpdate(long id, long amount);
}
