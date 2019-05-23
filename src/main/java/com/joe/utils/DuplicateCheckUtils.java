package com.joe.utils;

import com.google.common.hash.Funnels;

import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by joe on 2019/5/22
 */
public class DuplicateCheckUtils {

    /**
     * 缓存域名对应的helper
     */
    private static final Map<String, StringBloomFilterHelper> HELPER_CACHE = new HashMap<>();

    private static final MessageFormat KEY_FORMAT = new MessageFormat("DUPLICATE_{0}");


    static {
        // （例子）添加指定命名空间的存储配置
//        HELPER_CACHE.put("www.baidu.com", new StringBloomFilterHelper(10000000L, 0.00001, (long) (24 * 60 * 60)));
    }


    /**
     * url 添加到查重，返回该记录是否已存在
     *
     * @return 该记录是否存在
     */
    public static boolean addUrlDuplicate(JedisOpt jedisOpt, String url) {
        String domain = UrlUtils.extraDomain(url);
        return addDuplicate(jedisOpt, domain, url);
    }


    /**
     * 字符串添加到查重，返回该记录是否已存在
     *
     * @return 记录是否已存在
     */
    public static boolean addDuplicate(JedisOpt jedisOpt, String nameSpace, String value) {
        String key = KEY_FORMAT.format(new Object[]{nameSpace});

        boolean isNewKey = true;
        boolean isExists = true;
        int[] offset = getHelper(nameSpace).murmurHashOffset(value);
        for (int i : offset) {
            if (jedisOpt.setbit(key, i, true)) {
                isNewKey = false;
            } else {
                isExists = false;
            }
        }
        if (isNewKey) {
            setExpirationTime(jedisOpt, nameSpace, key);
        }

        return isExists;
    }


    /**
     * 指定 url 是否存在
     */
    public static boolean isUrlExists(JedisOpt jedisOpt, String url) {
        String domain = UrlUtils.extraDomain(url);
        return isExists(jedisOpt, domain, url);
    }

    /**
     * 字符串在指定命名空间里是否存在
     */
    public static boolean isExists(JedisOpt jedisOpt, String nameSpace, String value) {
        String duplicateKey = KEY_FORMAT.format(new Object[]{nameSpace});

        int[] offset = getHelper(nameSpace).murmurHashOffset(value);
        for (int i : offset) {
            if (!jedisOpt.getbit(duplicateKey, i)) {
                return false;
            }
        }

        return true;
    }


    /**
     * 添加查重配置
     *
     * @param nameSpace 查重的命名空间
     * @param capacity  最大数据量
     * @param errorRate 容错率
     * @param aliveTime nameSpace的存活时间（0 则永久存在）
     */
    public static void addConf(String nameSpace, long capacity, double errorRate, long aliveTime) {
        HELPER_CACHE.put(nameSpace, new StringBloomFilterHelper(capacity, errorRate, aliveTime));
    }


    private static StringBloomFilterHelper getHelper(String nameSpace) {
        StringBloomFilterHelper helper;
        if ((helper = HELPER_CACHE.get(nameSpace)) == null) {
            helper = StringBloomFilterHelper.DEF_HELPER;
        }
        return helper;
    }


    private static void setExpirationTime(JedisOpt jedisOpt, String nameSpace, String key) {
        long aliveTime = getHelper(nameSpace).aliveTime;

        if (aliveTime > 0 && jedisOpt.ttl(key) == -1) {
            jedisOpt.expire(key, (int) aliveTime);
        }
    }


    private static class StringBloomFilterHelper extends BloomFilterHelper<CharSequence> {

        /**
         * 对应查重的存活时间(s)，（小于等于0 则永久存活）
         */
        final long aliveTime;

        static final StringBloomFilterHelper DEF_HELPER = new StringBloomFilterHelper(10000000L, 0.0001, (long) 24 * 60 * 60);

        StringBloomFilterHelper(long capacity, double errorRate, Long aliveTime) {
            super(Funnels.stringFunnel(Charset.forName("utf-8")), capacity, errorRate);
            this.aliveTime = aliveTime;
        }
    }


}
