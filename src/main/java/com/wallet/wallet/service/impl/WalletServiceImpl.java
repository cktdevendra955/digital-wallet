package com.wallet.wallet.service.impl;

import com.wallet.common.exception.InsufficientBalanceException;
import com.wallet.common.exception.ResourceNotFoundException;
import com.wallet.user.entity.User;
import com.wallet.user.repository.UserRepository;
import com.wallet.wallet.dto.AddMoneyRequest;
import com.wallet.wallet.dto.WalletResponse;
import com.wallet.wallet.entity.Wallet;
import com.wallet.wallet.repository.WalletRepository;
import com.wallet.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    @Override
    public WalletResponse getWalletByUserEmail(String email) {
        User user = getUserByEmail(email);
        Wallet wallet = getWalletEntityByUserId(user.getId());
        return mapToResponse(wallet, user);
    }

    @Override
    @Transactional
    public WalletResponse addMoney(String email, AddMoneyRequest request) {
        User user = getUserByEmail(email);
        Wallet wallet = getWalletEntityByUserId(user.getId());

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        walletRepository.save(wallet);

        return mapToResponse(wallet, user);
    }

    @Override
    public Wallet getWalletEntityByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for this user"));
    }

    @Override
    public Wallet lockWalletForUpdate(Long walletId) {
        return walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
    }

    @Override
    public void debit(Wallet wallet, BigDecimal amount) {
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in wallet");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);
    }

    @Override
    public void credit(Wallet wallet, BigDecimal amount) {
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private WalletResponse mapToResponse(Wallet wallet, User user) {
        return WalletResponse.builder()
                .walletId(wallet.getId())
                .userId(user.getId())
                .userName(user.getName())
                .balance(wallet.getBalance())
                .build();
    }
}
