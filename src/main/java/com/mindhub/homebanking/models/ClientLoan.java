package com.mindhub.homebanking.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
public class ClientLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private Double amount;
    private Integer totalPayments;
    private Integer remainingPayments;
    private Double paymentAmount;
    private Double requestedAmount;

    @OneToMany(mappedBy = "clientLoan", fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<PayOut> payOutController = new LinkedList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    @ToString.Exclude
    private Client client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "loan_id")
    private Loan loan;


    public ClientLoan(Double amount, Integer payments, Client client, Loan loan) {
        this.amount = amount;
        this.totalPayments = payments;
        this.client = client;
        this.loan = loan;
        this.remainingPayments = payments;
    }

}
