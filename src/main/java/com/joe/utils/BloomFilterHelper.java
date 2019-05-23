package com.joe.utils;

import com.google.common.hash.Funnel;
import com.google.common.hash.Hashing;

/**
 * Create by joe on 2019/5/22
 */
public class BloomFilterHelper<T> {

    private int numHashFunctions;

    private int bitSize;

    private final Funnel<T> funnel;

    /**
     * 查重对应的空间容量
     */
    public final long capacity;

    /**
     * 容错率
     * <p>
     * 容错率越低，占用空间越大
     */
    public final double errorRate;


    public BloomFilterHelper(Funnel<T> funnel, long capacity, double errorRate) {
        if (funnel == null) {
            throw new RuntimeException("Funnel can not be null!");
        }
        this.funnel = funnel;
        this.capacity = capacity;
        this.errorRate = errorRate;
        bitSize = optimalNumOfBits(capacity, errorRate);
        numHashFunctions = optimalNumOfHashFunctions(capacity, bitSize);
    }

    /**
     * 计算hash offset数组
     */
    public int[] murmurHashOffset(T value) {
        int[] offset = new int[numHashFunctions];

        long hash64 = Hashing.murmur3_128().hashObject(value, funnel).asLong();
        int hash1 = (int) hash64;
        int hash2 = (int) (hash64 >>> 32);
        for (int i = 1; i <= numHashFunctions; i++) {
            int nextHash = hash1 + i * hash2;
            if (nextHash < 0) {
                nextHash = ~nextHash;
            }
            offset[i - 1] = nextHash % bitSize;
        }

        return offset;
    }

    /**
     * 计算bit数组长度
     */
    private int optimalNumOfBits(long n, double p) {
        if (p == 0) {
            p = Double.MIN_VALUE;
        }
        return (int) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }

    /**
     * 计算hash方法执行次数
     */
    private int optimalNumOfHashFunctions(long n, long m) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }
}