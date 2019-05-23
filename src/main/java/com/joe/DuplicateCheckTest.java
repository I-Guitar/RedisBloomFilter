package com.joe;

import com.joe.utils.DuplicateCheckUtils;
import com.joe.utils.JedisOpt;
import redis.clients.jedis.Jedis;

/**
 * Create by joe on 2019/5/23
 * <p>
 * 简单使用说明
 */
public class DuplicateCheckTest {


    public static void main(String[] args) {
        JedisOpt jedisOpt = new MyJedisOpt();


        // url 查重
        String url = "https://www.xunleicang.com/vod-read-id-9817.html";
        boolean isExists = DuplicateCheckUtils.addUrlDuplicate(jedisOpt, url);  // 添加并返回查重
        boolean isExists2 = DuplicateCheckUtils.isUrlExists(jedisOpt, url);  // 查询是否存在


        // 字符串查重
        String nameSpace = "defaultNameSpace";
        String str = "她冲向甲板，试图跳入大海结束一生。杰克及时发现并且在关键时刻以自己的真诚和独到的幽默说服了罗丝。尽管卡尔很不情愿，但为答谢杰克的救妻之恩";
        DuplicateCheckUtils.addConf(nameSpace, 10000000L, 0.00001, 0);  // 不设置指定命名空间配置，则使用默认配置
        boolean isExists3 = DuplicateCheckUtils.addDuplicate(jedisOpt, nameSpace, str);  // 添加并返回查重
        boolean isExists4 = DuplicateCheckUtils.isExists(jedisOpt, nameSpace, str);// 查询是否存在


    }

    static class MyJedisOpt implements JedisOpt {
        Jedis jedis = new Jedis();

        @Override
        public Boolean setbit(String key, long offset, boolean value) {
            return jedis.setbit(key, offset, value);
        }

        @Override
        public Boolean getbit(String key, long offset) {
            return jedis.getbit(key, offset);
        }

        @Override
        public Long ttl(String key) {
            return jedis.ttl(key);
        }

        @Override
        public Long expire(String key, int seconds) {
            return jedis.expire(key, seconds);
        }
    }


}
