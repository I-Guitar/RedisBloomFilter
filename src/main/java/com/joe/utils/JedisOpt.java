package com.joe.utils;

/**
 * Create by joe on 2019/5/22
 */
public interface JedisOpt {

    Boolean setbit(String key, long offset, boolean value);

    Boolean getbit(String key, long offset);

    Long ttl(String key);

    Long expire(String key, int seconds);
}
