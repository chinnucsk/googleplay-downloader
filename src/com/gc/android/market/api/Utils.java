package com.gc.android.market.api;

import java.util.Random;

import com.google.gson.Gson;

public class Utils {
    public static final Gson gson = new Gson();
    
    public static final Random random = new Random();
    
    public static long nextLong() {
        long bits, val;
        do {
            bits = (random.nextLong() << 1) >>> 1;
            val = bits % 10000000000000L;
        } while (bits - val + (10000000000000L - 1) < 0L);
        return val + 30000000000000L;
    }

}
