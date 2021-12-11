package com.mindhub.homebanking.controllers;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import com.mindhub.homebanking.enums.AccountType;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.implement.PDFServiceImpl;
import org.springframework.http.HttpStatus;
import com.mindhub.homebanking.models.Client;
import org.springframework.http.ResponseEntity;
import com.mindhub.homebanking.dtos.AccountDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.mindhub.homebanking.services.implement.AccountServiceImpl;

import javax.servlet.http.HttpServletResponse;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@RestController
@RequestMapping("/api")
public class AccountController {


    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountServiceImpl accountServiceImpl;
    @Autowired
    private PDFServiceImpl pdfService;

    //Revisar ruta para evitar q usuarios autenticados
    @GetMapping("/accounts")
    public Set<AccountDTO> getAccounts() {
        return accountServiceImpl.getAccounts();
    }

    @GetMapping("/accounts/{id}")
    public AccountDTO getAccountDTO(HttpServletResponse response, Authentication auth, @PathVariable Long id) throws IOException {
        Client client = clientRepository.findByEmail(auth.getName()).orElse(null);
        Account account = accountRepository.findById(id).orElse(null);

        if (!client.getAccounts().contains(account) || !account.getEnabled()) {
            response.sendError(401);
            return null;
        }
        return accountServiceImpl.getAccountById(id);
    }

    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> addAccount(Authentication auth, String accountType) {

        Client client = clientRepository.findByEmail(auth.getName()).orElse(null);

        //Checking if client exists or it's email is validated through token
        if (client == null || !client.isEnabled()) {
            return new ResponseEntity<>("not found OR not validated", HttpStatus.FORBIDDEN);
        }

        //Amount of accounts limited as requested
        if (client.getAccounts().size() == 3) {
            return new ResponseEntity<>("Account limit reached for this client", HttpStatus.FORBIDDEN);
        }

        try {
            AccountType accType = AccountType.valueOf(accountType);
            if (accountServiceImpl.createAccount(client, accType)) {
                return new ResponseEntity<>("CREATED", HttpStatus.CREATED);
            }
        } catch (IllegalArgumentException exc) {
            return new ResponseEntity<>("Inexisting Account Type", HttpStatus.BAD_REQUEST);
        }


        //In case something unexpected occurs send ERROR
        return new ResponseEntity<>("Something went wrong, please contact Support Service", HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @PostMapping("/account/summary")
    public void getAccountSummary(HttpServletResponse response, Authentication auth, String accountNumber) throws IOException {
        Client client = clientRepository.findByEmail(auth.getName()).orElse(null);
        Account account = accountRepository.findByNumber(accountNumber).orElse(null);

        if (!client.getAccounts().contains(account) || !account.getEnabled()) {
            response.sendRedirect("/index.html");
            return;
        }
        response.setContentType("application/pdf");
        String date = LocalDateTime.now().format(ISO_LOCAL_DATE);
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + "AC-SUMMARY-" + accountNumber + client.getLast_name().toUpperCase() + ".pdf";
        response.setHeader(headerKey, headerValue);

        LinkedHashSet<Transaction> transactions = new LinkedHashSet<>(account.getTransactions());


        pdfService.generatePDF(response, client, accountNumber, transactions);

    }

    @PostMapping("/account/delete")
    public ResponseEntity<String> logicAccountDeletion(HttpServletResponse response, Authentication auth, String accountNumber) throws IOException {
        Client client = clientRepository.findByEmail(auth.getName()).orElse(null);
        Account account = accountRepository.findByNumber(accountNumber).orElse(null);

        if (!client.getAccounts().contains(account)) {
            return new ResponseEntity<String>("Account doesn't belong to authenticated client", HttpStatus.FORBIDDEN);
        }

        if (accountServiceImpl.deleteAccount(account)) {
            return new ResponseEntity<String>("Deletion succeed, please wait confirmation from our agents", HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<String>("Something wrong happened", HttpStatus.FORBIDDEN);

    }

}
