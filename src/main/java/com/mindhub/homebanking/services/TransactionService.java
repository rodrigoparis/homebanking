package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;

public interface TransactionService {

    String createTransaction(String originAccountNumber, String destinationAccountNumber, Double amount, String description, Authentication auth);
    String filterTransactions(HttpServletResponse response, Client client, String accountNumber, LocalDateTime fromDate, LocalDateTime toDate) throws IOException;

    String payOut(String receiver,Client client, Double amount, String destinationAccount, String description, Card paymentCard);
}
