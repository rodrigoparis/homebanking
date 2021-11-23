package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;

public interface LoanService {
    public void createLoan(Double requestedAmount, Integer payments, String loanName, Client client, Account account);
}
