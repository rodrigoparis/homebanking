package com.mindhub.homebanking.services.implement;

import java.time.LocalDateTime;

import com.mindhub.homebanking.services.CardService;
import lombok.AllArgsConstructor;
import com.mindhub.homebanking.enums.*;
import com.mindhub.homebanking.models.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import com.mindhub.homebanking.repositories.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@AllArgsConstructor
public class CardServiceImpl implements CardService {

    @Autowired
    private CardRepository cardRepository;


    @Override
    public boolean createCard(String cardColor, String cardType, Client client) {


        //Checking card limit as required
        if (client.getCards().stream().filter(card -> card.getType().toString().equals(cardType)).count() == 3) {
            return false;
        }

        //Asigning random number to the Card
        String cardNumber = getCardNumber();
        String cvv = getCvv();

        Card card = new Card();
        card.setClient(client);
        card.setCardHolder();
        card.setColor(CardColor.valueOf(cardColor));
        card.setType(CardType.valueOf(cardType));
        card.setFromDate(LocalDateTime.now());
        card.setThruDate(LocalDateTime.now().plusYears(5));
        card.setCvv(cvv);
        card.setNumber(cardNumber);
        card.setEnabled(true);

        cardRepository.save(card);

        return true;
    }

    @NotNull
    private static String getCvv() {
        return ((Integer) (int) ((Math.random() * (999 - 100)) + 100)).toString();
    }

    @NotNull
    private static String getCardNumber() {
        String cardNumber = "";
        Integer randomNumber;
        for (int i = 0; i < 4; i++) {
            randomNumber = (int) ((Math.random() * (9999 - 1000)) + 1000);
            cardNumber = cardNumber.concat(randomNumber.toString());
            if (i != 3) {
                cardNumber = cardNumber.concat("-");
            }
        }
        return cardNumber;
    }

    @Override
    public boolean deleteCard(Card card) {
        card.setEnabled(false);
        cardRepository.save(card);
        return true;
    }
}
