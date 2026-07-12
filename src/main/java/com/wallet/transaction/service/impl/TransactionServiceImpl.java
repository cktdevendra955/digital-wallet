package com.wallet.transaction.service.impl;

import com.wallet.common.exception.ResourceNotFoundException;
import com.wallet.common.exception.WalletException;
import com.wallet.transaction.dto.TransactionResponse;
import com.wallet.transaction.dto.TransferRequest;
import com.wallet.transaction.entity.Transaction;
import com.wallet.transaction.entity.TransactionType;
import com.wallet.transaction.repository.TransactionRepository;
import com.wallet.transaction.service.TransactionService;
import com.wallet.user.entity.User;
import com.wallet.user.repository.UserRepository;
import com.wallet.wallet.entity.Wallet;
import com.wallet.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TransactionResponse transfer(String senderEmail, TransferRequest request) {

        if (senderEmail.equalsIgnoreCase(request.getReceiverEmail())) {
            throw new WalletException("You cannot transfer money to yourself");
        }

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        User receiver = userRepository.findByEmail(request.getReceiverEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found with this email"));

        // pessimistic lock on sender's wallet row so two simultaneous transfers
        // from the same account can't both read the same balance and overdraw it
        Wallet senderWallet = walletService.lockWalletForUpdate(
                walletService.getWalletEntityByUserId(sender.getId()).getId());
        Wallet receiverWallet = walletService.getWalletEntityByUserId(receiver.getId());

        // debit first: if balance is insufficient this throws and the whole
        // @Transactional method rolls back, so no partial transfer is ever saved
        walletService.debit(senderWallet, request.getAmount());
        walletService.credit(receiverWallet, request.getAmount());

        Transaction transaction = Transaction.builder()
                .senderWallet(senderWallet)
                .receiverWallet(receiverWallet)
                .amount(request.getAmount())
                .type(TransactionType.TRANSFER)
                .status("SUCCESS")
                .description(request.getDescription())
                .build();

        Transaction saved = transactionRepository.save(transaction);

        return mapToResponse(saved, sender.getEmail(), receiver.getEmail());
    }

    @Override
    public Page<TransactionResponse> getTransactionHistory(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Wallet wallet = walletService.getWalletEntityByUserId(user.getId());

        return transactionRepository.findAllByWalletId(wallet.getId(), pageable)
                .map(txn -> {
                    String senderEmail = txn.getSenderWallet() != null ? txn.getSenderWallet().getUser().getEmail() : null;
                    String receiverEmail = txn.getReceiverWallet() != null ? txn.getReceiverWallet().getUser().getEmail() : null;
                    return mapToResponse(txn, senderEmail, receiverEmail);
                });
    }

    private TransactionResponse mapToResponse(Transaction txn, String senderEmail, String receiverEmail) {
        return TransactionResponse.builder()
                .transactionId(txn.getId())
                .senderEmail(senderEmail)
                .receiverEmail(receiverEmail)
                .amount(txn.getAmount())
                .type(txn.getType())
                .status(txn.getStatus())
                .description(txn.getDescription())
                .timestamp(txn.getTimestamp())
                .build();
    }
}
