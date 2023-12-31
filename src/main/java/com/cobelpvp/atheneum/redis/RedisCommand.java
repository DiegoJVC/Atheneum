package com.cobelpvp.atheneum.redis;

import redis.clients.jedis.Jedis;

public interface RedisCommand<T> {
    T execute(final Jedis p0);
}
