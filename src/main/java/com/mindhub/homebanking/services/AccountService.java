package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Client;

import java.util.Optional;
import java.util.Set;

public interface AccountService {
    Set<AccountDTO> getAccounts();
    AccountDTO getAccountById(Long id);
    public boolean createAccount(Client client);
    AccountDTO getAccountByNumber(String number);
}
