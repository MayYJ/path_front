package com.may.routeplansystem.util;

import java.util.Random;

/**
 * 建议在需要大量使用随机数的时候使用此类，避免重复创建Random对象
 * @author 10587
 */
public class RandomInt {

    private RandomInt() {}

    private static Random random = new Random();

    public static int randomIntExceptZero(int bound) {
        if (bound <= 1) {
            throw  new IllegalArgumentException("bound must be positive");
        }
        int num = 0;
        while (num == 0) {
            num = random.nextInt(bound);
        }
        return num;
    }

    public static int randomInt(int start, int end) {
        if (start < 0) {
            throw  new IllegalArgumentException("bound must be positive");
        }
        return random.nextInt(end - start) + start;
    }




}
