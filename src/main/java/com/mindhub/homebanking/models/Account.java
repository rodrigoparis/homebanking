package com.mindhub.homebanking.models;


import com.mindhub.homebanking.enums.AccountType;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@ToString
@Getter @Setter
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long account_id;

    private String number;
    private LocalDateTime creationDate;
    private Double balance;
    private Boolean enabled;
    private AccountType accountType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    @ToString.Exclude
    private Client client;

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Transaction> transactions = new ArrayList<>();

    public Account(String number, LocalDateTime creationDate, Double balance, Client client, AccountType accountType) {
        this.number = number;
        this.creationDate = creationDate;
        this.balance = balance;
        this.client = client;
        this.enabled = true;
        this.accountType = accountType;
    }

    @JsonIgnore
    public Client getClient() {
        return client;
    }

    public void addTransaction(Transaction transaction) {
        transaction.setAccount(this);
        transactions.add(transaction);
    }


}
