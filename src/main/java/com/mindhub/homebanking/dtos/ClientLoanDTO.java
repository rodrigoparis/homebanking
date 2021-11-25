package com.mindhub.homebanking.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.mindhub.homebanking.models.ClientLoan;


@Data
@NoArgsConstructor
public class ClientLoanDTO {

    private Long clientLoan_id;
    private Long loan_id;
    private String name;
    private Double amount;
    private Integer payments;
    private Integer remainingPayments;
    private Double paymentAmount;
    private Double requestedAmount;

    public ClientLoanDTO(ClientLoan clientLoan) {
        this.clientLoan_id = clientLoan.getId();
        this.loan_id = clientLoan.getLoan().getId();
        this.name = clientLoan.getLoan().getName();
        this.amount = clientLoan.getAmount();
        this.payments = clientLoan.getTotalPayments();
        this.remainingPayments = clientLoan.getRemainingPayments();
        this.paymentAmount = clientLoan.getPaymentAmount();
        this.requestedAmount = clientLoan.getRequestedAmount();

    }


}
