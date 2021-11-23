package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.enums.TransactionType;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import com.mindhub.homebanking.services.implement.LoanServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class LoanController {

    @Autowired
    ClientLoanRepository clientLoanRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    LoanServiceImpl loanServiceImpl;

    @PostMapping("/loans")
    @Transactional
    public ResponseEntity<String> createLoan(Authentication auth,
                                             @RequestBody LoanApplicationDTO application) {
        Double requestedAmount = application.getAmount();
        Integer payments = application.getPayments();
        String loanName = application.getName();
        String accountNumber = application.getAccountNumber();

        if (loanName.isBlank() || accountNumber.isBlank() || requestedAmount.isNaN() || payments == null) {
            return new ResponseEntity<>("None parameter can be empty", HttpStatus.FORBIDDEN);

        }
        if (requestedAmount.isNaN() || requestedAmount <= 0) {
            return new ResponseEntity<>("Not valid requested amount", HttpStatus.FORBIDDEN);
        }

        if (payments <= 0) {
            return new ResponseEntity<>("Requested payments not available", HttpStatus.FORBIDDEN);
        }

        Loan loan = loanRepository.findByName(loanName).orElse(null);
        if (loan == null) {
            return new ResponseEntity<>("Not valid loan", HttpStatus.FORBIDDEN);
        }

        if (requestedAmount > loan.getMaxAmount()) {
            return new ResponseEntity<>("Requested amount excedeed max amount", HttpStatus.FORBIDDEN);
        }

        if (!loan.getPayments().contains(payments)) {
            return new ResponseEntity<>("Not valid payment selection", HttpStatus.FORBIDDEN);
        }

        if (!accountRepository.existsByNumber(accountNumber)) {
            return new ResponseEntity<>("Destination account doesn't exist", HttpStatus.FORBIDDEN);
        }

        Client client = clientRepository.findByEmail(auth.getName()).orElse(null);
        Account account = accountRepository.findByNumber(accountNumber).orElse(null);

        if (!client.getAccounts().contains(account)) {
            return new ResponseEntity<>("Destination account must belong to authenticated client", HttpStatus.FORBIDDEN);
        }

        loanServiceImpl.createLoan(requestedAmount, payments, loanName, client, account);

        return new ResponseEntity<>("Accepted Loan", HttpStatus.CREATED);

    }

    @GetMapping("/loans") //Devuelve los prestamos disponibles
    public List<LoanDTO> getAvailableLoans() {
        return loanServiceImpl.getLoans();
    }

    @Transactional
    @PostMapping("/loans/payOut") //Devuelve los prestamos disponibles
    public ResponseEntity<String> payOut(Authentication auth, String accountNumber, Long clientLoanID, Integer quantity) {
        Client client = clientRepository.findByEmail(auth.getName()).orElse(null);

        if (client == null)
            return new ResponseEntity<>("Not authenticated Client", HttpStatus.FORBIDDEN);

        if (!client.getAccounts().stream().anyMatch(account -> account.getNumber().equals(accountNumber))) {
            return new ResponseEntity<>("Selected account not belongs to client", HttpStatus.FORBIDDEN);
        }


        String success = loanServiceImpl.payOut(clientLoanID, accountNumber, quantity);

        if (success.contains("successful")) {
            return new ResponseEntity<>(success, HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>(success, HttpStatus.BAD_REQUEST);

    }


}
