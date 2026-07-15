package com.wallet.common.util;


import java.util.Random;

public class WalletNumberGenerator {

    private WalletNumberGenerator() {

    }

    public static String generateWalletNumber() {
        Random random = new Random();
        return "WAL" + (100000000 + random.nextInt(900000000));
    }
}
