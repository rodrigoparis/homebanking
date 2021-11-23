package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Client;
import org.springframework.security.core.Authentication;


public interface CardService {

    boolean createCard(String cardColor, String cardType, Client client);
    boolean deleteCard(Authentication auth);
}
