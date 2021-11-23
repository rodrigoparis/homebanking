package com.mindhub.homebanking.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class LoanApplicationDTO {

    private Long loanId;
    private String name;
    private Double amount;
    private Integer payments;
    private String accountNumber;

}
