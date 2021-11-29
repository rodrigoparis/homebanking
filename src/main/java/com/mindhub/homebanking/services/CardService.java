package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Card;


public interface CardService {

    boolean createCard(String cardColor, String cardType, Client client);
    boolean deleteCard(Card card);
}
