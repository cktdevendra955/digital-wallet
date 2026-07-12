package com.wallet.transaction.dto;

import com.wallet.transaction.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Long transactionId;
    private String senderEmail;
    private String receiverEmail;
    private BigDecimal amount;
    private TransactionType type;
    private String status;
    private String description;
    private LocalDateTime timestamp;
}
