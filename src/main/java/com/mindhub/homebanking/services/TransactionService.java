package com.mindhub.homebanking.services;

import org.springframework.security.core.Authentication;

public interface TransactionService {

    String createTransaction(String originAccountNumber, String destinationAccountNumber, Double amount, String description, Authentication auth);
}
