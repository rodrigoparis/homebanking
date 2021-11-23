package com.mindhub.homebanking.dtos;

import lombok.Data;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.enums.TransactionType;


@Data
@NoArgsConstructor
public class TransactionDTO {

    private Long id;
    private TransactionType type;
    private Double amount;
    private String description;
    private LocalDateTime date;
    private String originAccount;
    private String destinationAccount;
    private Double currentBalance;
    private String receiver;
    private String sender;

    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.type = transaction.getType();
        this.amount = transaction.getAmount();
        this.description = transaction.getDescription();
        this.date = transaction.getDate();
        this.currentBalance = transaction.getCurrentBalance();
        this.destinationAccount = transaction.getDestinationAccount();
        this.originAccount = transaction.getOriginAccount();
        this.receiver = transaction.getReceiver();
        this.sender = transaction.getSender();
    }


}
