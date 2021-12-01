package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.enums.AccountType;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;

import java.util.Optional;
import java.util.Set;

public interface AccountService {
    Set<AccountDTO> getAccounts();
    AccountDTO getAccountById(Long id);
    boolean createAccount(Client client, AccountType accountType);
    AccountDTO getAccountByNumber(String number);
    boolean deleteAccount(Account account);
}
