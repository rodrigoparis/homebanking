package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.Transaction;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

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

    @PostMapping("/transactions/filterTransactions")
    public ResponseEntity<String> getFilteredTransactions(HttpServletResponse response, Authentication auth, String accountNumber, String from, String to) throws IOException {
        LocalDateTime fromDate = LocalDateTime.parse(from, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime toDate = LocalDateTime.parse(to, DateTimeFormatter.ISO_DATE_TIME);
        Client client = clientRepository.findByEmail(auth.getName()).orElse(null);
        response.setContentType("application/pdf");
        String date = LocalDateTime.now().format(ISO_LOCAL_DATE);
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + "AC-SUMMARY-" + accountNumber + client.getLast_name().toUpperCase() + ".pdf";
        response.setHeader(headerKey, headerValue);

        String message = transactionServiceImpl.filterTransactions(response, client, accountNumber, fromDate, toDate);

        if (message.contains("success")) {

            return new ResponseEntity<>("Filter successful", HttpStatus.OK);

        }


        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);

    }


    @Transactional
    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("/transactions/cardPayment")
    public ResponseEntity<String> payOut(Double amount, String description, String cvv, String thruDate, String email) {
        //chequear que esto funcione
        Client client = clientRepository.findByEmail(email).orElse(null);

        String receiver = "Melba Trips";
        String destinationAccount = "VIN-001";

        if (client == null) {
            return new ResponseEntity<>("Not valid client", HttpStatus.FORBIDDEN);
        }

        Card paymentCard = null;

        for (Card card : client.getCards()) {
            if (card.getCvv().equals(cvv)) {
                paymentCard = card;
                break;
            }
        }

        if (paymentCard == null) {
            return new ResponseEntity<>("Card not found", HttpStatus.FORBIDDEN);
        }

        String success = transactionServiceImpl.payOut(receiver, client, amount, destinationAccount, description, paymentCard);

        if (success.contains("successful")) {
            return new ResponseEntity<>(success, HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>(success, HttpStatus.BAD_REQUEST);

    }
}
