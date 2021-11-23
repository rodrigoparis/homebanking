package com.mindhub.homebanking.dtos;

import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.HashSet;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;
import java.util.stream.Collectors;
import com.mindhub.homebanking.models.Account;

@Data
@NoArgsConstructor
public class AccountDTO {

    private Long account_id;
    private String number;
    private LocalDateTime creationDate;
    private Double balance;
    private Set<TransactionDTO> transactions = new LinkedHashSet<>();

    public AccountDTO(Account account) {
        this.account_id = account.getAccount_id();
        this.number = account.getNumber();
        this.creationDate = account.getCreationDate();
        this.balance = account.getBalance();
        this.transactions = account.getTransactions().stream().map(TransactionDTO::new).collect(Collectors.toSet());
    }
}
