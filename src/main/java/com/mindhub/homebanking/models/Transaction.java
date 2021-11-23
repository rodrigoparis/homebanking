package com.mindhub.homebanking.models;

import com.mindhub.homebanking.enums.TransactionType;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Comparator;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Transaction implements Comparable<Transaction> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;


    public Transaction(TransactionType type, Double amount, String description, LocalDateTime date, String originAccount, String destinationAccount, String receiver, String sender) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
        this.receiver = receiver;
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "Transaction" + '\n' +
                "Id: " + id + '\n' +
                "Type: " + type + '\n' +
                "Amount: " +  amount+ '\n' +
                "Description: " + description + '\n' +
                "Date: " + date;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int compareTo(@NotNull Transaction o) {
        return this.type.compareTo(o.type);
    }
}
