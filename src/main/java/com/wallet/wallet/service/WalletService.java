package com.wallet.wallet.service;

import com.wallet.wallet.dto.AddMoneyRequest;
import com.wallet.wallet.dto.WalletResponse;
import com.wallet.wallet.entity.Wallet;

import java.math.BigDecimal;

public interface WalletService {

    WalletResponse getWalletByUserEmail(String email);

    WalletResponse addMoney(String email, AddMoneyRequest request);

    // used internally by transaction feature
    Wallet getWalletEntityByUserId(Long userId);

    Wallet lockWalletForUpdate(Long walletId);

    void debit(Wallet wallet, BigDecimal amount);

    void credit(Wallet wallet, BigDecimal amount);
}
