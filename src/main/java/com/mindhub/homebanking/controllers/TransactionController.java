package com.mindhub.homebanking.controllers;

import org.springframework.http.HttpStatus;
import com.mindhub.homebanking.models.Client;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.mindhub.homebanking.repositories.AccountRepository;
import org.springframework.transaction.annotation.Transactional;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.implement.TransactionServiceImpl;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TransactionServiceImpl transactionServiceImpl;

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> makeTransaction(
            @RequestParam String originAccount, @RequestParam String destinationAccount,
            @RequestParam Double amount, @RequestParam String description, Authentication auth) {

        if (originAccount == null || destinationAccount == null || amount == null || description == null) {
            return new ResponseEntity<>("No parameter can be null", HttpStatus.FORBIDDEN);
        }

        if (originAccount.isEmpty() || destinationAccount.isEmpty() || description.isEmpty() || amount.isNaN()) {
            return new ResponseEntity<>("No parameter can be empty", HttpStatus.FORBIDDEN);
        }

        if (originAccount.equalsIgnoreCase(destinationAccount)) {
            return new ResponseEntity<>("Destination and origin account cannot be the same", HttpStatus.FORBIDDEN);
        }

        boolean accountExists = transactionServiceImpl.accountExists(originAccount);
        if (!accountExists) {
            return new ResponseEntity<>("Origin account not found", HttpStatus.FORBIDDEN);
        }

        String response = transactionServiceImpl.createTransaction(originAccount, destinationAccount, amount, description, auth);

        if (response.equalsIgnoreCase("created")) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }


        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @PostMapping("/transactions/agenda/add")
    public ResponseEntity<String> addToAgenda(@RequestParam String accountNumber, Authentication auth) {

        Client client = clientRepository.findByEmail(auth.getName()).orElse(null);

        if (client != null) {
            boolean success = transactionServiceImpl.addAccount(accountNumber, client);
            if (success) return new ResponseEntity<>("Account number added to agenda", HttpStatus.OK);

        }
        return new ResponseEntity<>("Impossible to add new account to your agenda", HttpStatus.BAD_REQUEST);

    }


}
