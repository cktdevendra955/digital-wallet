package com.wallet.wallet.controller;

import com.wallet.common.response.ApiResponse;
import com.wallet.wallet.dto.AddMoneyRequest;
import com.wallet.wallet.dto.WalletResponse;
import com.wallet.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@Tag(name = "Wallet", description = "Wallet balance and top-up APIs")
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    @Operation(summary = "Get wallet balance", description = "Returns the current logged-in user's wallet details")
    public ApiResponse<WalletResponse> getBalance(Authentication authentication) {
        String email = authentication.getName();
        WalletResponse response = walletService.getWalletByUserEmail(email);
        return ApiResponse.success("Wallet fetched successfully", response);
    }

    @PostMapping("/add-money")
    @Operation(summary = "Add money to wallet", description = "Simulates a top-up (no real payment gateway)")
    public ApiResponse<WalletResponse> addMoney(Authentication authentication,
                                                 @Valid @RequestBody AddMoneyRequest request) {
        String email = authentication.getName();
        WalletResponse response = walletService.addMoney(email, request);
        return ApiResponse.success("Money added successfully", response);
    }
}
