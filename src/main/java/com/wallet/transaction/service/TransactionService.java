package com.wallet.transaction.service;

import com.wallet.transaction.dto.TransactionResponse;
import com.wallet.transaction.dto.TransferRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

    TransactionResponse transfer(String senderEmail, TransferRequest request);

    Page<TransactionResponse> getTransactionHistory(String email, Pageable pageable);
}
