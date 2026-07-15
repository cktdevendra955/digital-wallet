package com.wallet.common.util;

import java.util.UUID;

public class TransactionIdGenerator {

    private TransactionIdGenerator() {

    }

    public static String generateTransactionId() {

        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}