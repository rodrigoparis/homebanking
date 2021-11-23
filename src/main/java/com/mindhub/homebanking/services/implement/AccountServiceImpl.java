package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public boolean createAccount (Client client) {

        Account account = new Account();

        account.setClient(client);
        account.setBalance(0D);
        account.setCreationDate(LocalDateTime.now());

        Integer accountCode = account.hashCode();
        Integer clientCode = client.hashCode();
        String accountNumber = "VIN-" + generateNumber(accountCode, clientCode);

        account.setNumber(accountNumber);
        accountRepository.save(account);

        return true;
    }

    @Override
    public AccountDTO getAccountByNumber(String number) {
        return null;
    }

    public String generateNumber(Integer num, Integer num2) {
        Integer accountNumber = Math.abs(num * num2);
        return accountNumber.toString().substring(0, 8);
    }

    @Override
    public Set<AccountDTO> getAccounts() {
        return accountRepository.findAll().stream().map(AccountDTO::new).collect(Collectors.toSet());
    }

    @Override
    public AccountDTO getAccountById(Long id) {
        return accountRepository.findById(id).map(AccountDTO::new).orElse(null);
    }




}
