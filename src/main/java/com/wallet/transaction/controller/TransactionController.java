package com.wallet.transaction.controller;

import com.wallet.common.response.ApiResponse;
import com.wallet.transaction.dto.TransactionResponse;
import com.wallet.transaction.dto.TransferRequest;
import com.wallet.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction", description = "Money transfer and transaction history APIs")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    @Operation(summary = "Transfer money", description = "Transfers money from the logged-in user's wallet to another user's wallet by email")
    public ApiResponse<TransactionResponse> transfer(Authentication authentication,
                                                       @Valid @RequestBody TransferRequest request) {
        String senderEmail = authentication.getName();
        TransactionResponse response = transactionService.transfer(senderEmail, request);
        return ApiResponse.success("Money transferred successfully", response);
    }

    @GetMapping("/history")
    @Operation(summary = "Get transaction history", description = "Returns paginated transaction history for the logged-in user's wallet")
    public ApiResponse<Page<TransactionResponse>> getHistory(Authentication authentication,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        String email = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponse> response = transactionService.getTransactionHistory(email, pageable);
        return ApiResponse.success("Transaction history fetched successfully", response);
    }
}
