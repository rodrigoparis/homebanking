package com.mindhub.homebanking.services.implement;


import java.time.LocalDateTime;

import com.mindhub.homebanking.models.*;
import org.springframework.http.HttpStatus;
import com.mindhub.homebanking.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import com.mindhub.homebanking.enums.TransactionType;
import org.springframework.security.core.Authentication;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountServiceImpl accountServiceImpl;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public String createTransaction(String originAccountNumber, String destinationAccountNumber, Double amount, String description, Authentication auth) {



        Client client = clientRepository.findByEmail(auth.getName()).orElse(null);
        Account originAccount = accountRepository.findByNumber(originAccountNumber).orElse(null);

        if (!client.getAccounts().contains(originAccount)) {
            return "Origin account is not an account of the authenticated client";
        }

        Account destinationAccount = accountRepository.findByNumber(destinationAccountNumber).orElse(null);

        if (destinationAccount == null) {
            return "Destination account doesn't exists";
        }

        if (!(originAccount.getBalance() >= Math.abs(amount))) {
            return "Origin account lacks funds";
        }

        String receiver = destinationAccount.getClient().getFirst_name() + " " + destinationAccount.getClient().getLast_name();
        String sender = originAccount.getClient().getFirst_name() + " " + originAccount.getClient().getLast_name();

        Transaction debitTransaction = new Transaction(
                TransactionType.DEBIT,
                -amount,
                description,
                LocalDateTime.now(),
                originAccountNumber,
                destinationAccountNumber, receiver, sender);

        Transaction creditTransaction = new Transaction(
                TransactionType.CREDIT,
                amount,
                description,
                LocalDateTime.now(),
                originAccountNumber,
                destinationAccountNumber, receiver, sender);

        Double currentOriginBalance = originAccount.getBalance() - amount;
        Double currentDestinationBalance = destinationAccount.getBalance() + amount;

        debitTransaction.setCurrentBalance(currentOriginBalance);
        creditTransaction.setCurrentBalance(currentDestinationBalance);

        originAccount.addTransaction(debitTransaction);
        originAccount.setBalance(currentOriginBalance);

        destinationAccount.addTransaction(creditTransaction);
        destinationAccount.setBalance(currentDestinationBalance);

        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);
        accountRepository.save(originAccount);
        accountRepository.save(destinationAccount);

        return "created";

    }


    public boolean addAccount(String accountNumber, Client client) {

        if (!accountExists(accountNumber)) return false;
        Client contact = accountRepository.findByNumber(accountNumber).orElse(null).getClient();
        String name = contact.getFirst_name() + " " + contact.getLast_name();
        client.addToAgenda(accountNumber, name);
        clientRepository.save(client);
        return true;
    }

    public boolean accountExists(String originAccount) {
        return accountRepository.existsByNumber(originAccount);
    }

    public void transactionTest(String originAccountNumber, String destinationAccountNumber, Double amount, String description, LocalDateTime transactionDate) {

        Account originAccount = accountRepository.findByNumber(originAccountNumber).orElse(null);
        Account destinationAccount = accountRepository.findByNumber(destinationAccountNumber).orElse(null);
        String receiver = destinationAccount.getClient().getFirst_name() + " " + destinationAccount.getClient().getLast_name();
        String sender = originAccount.getClient().getFirst_name() + " " + originAccount.getClient().getLast_name();


        Transaction debitTransaction = new Transaction(
                TransactionType.DEBIT,
                -amount,
                description,
                transactionDate,
                originAccountNumber,
                destinationAccountNumber,
                receiver,
                sender);

        Transaction creditTransaction = new Transaction(
                TransactionType.CREDIT,
                amount,
                description,
                transactionDate,
                originAccountNumber,
                destinationAccountNumber,
                receiver,
                sender);


        Double currentOriginBalance = originAccount.getBalance() - amount;
        Double currentDestinationBalance = destinationAccount.getBalance() + amount;

        debitTransaction.setCurrentBalance(currentOriginBalance);
        creditTransaction.setCurrentBalance(currentDestinationBalance);

        originAccount.addTransaction(debitTransaction);
        originAccount.setBalance(currentOriginBalance);

        destinationAccount.addTransaction(creditTransaction);
        destinationAccount.setBalance(currentDestinationBalance);


        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);
        accountRepository.save(originAccount);
        accountRepository.save(destinationAccount);
    }

}
