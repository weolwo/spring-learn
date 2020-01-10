package com.poplar.utils;


import redis.clients.jedis.Jedis;

/**
 * by poplar created on 2020/1/9
 */
public class CacheTool {

    public static boolean setNx(String key, String value, int expireSeconds) {
        Jedis jedis = new Jedis("localhost", 6379);
        //nxxx NX|XX, NX -- Only set the key if it does not already exist. XX -- Only set the keyif it already exist.
        // expx EX|PX, expire time units: EX = seconds; PX = milliseconds
        //源码对这两个参数的说明
        String result = jedis.set(key, value, "NX", "EX", expireSeconds);
        jedis.close();
        return "OK".equalsIgnoreCase(result);
    }

    public static void pexpire(String key, long expireMilliseconds) {
        Jedis jedis = new Jedis("localhost", 6379);
        jedis.pexpire(key, expireMilliseconds);
        jedis.close();
    }
}
