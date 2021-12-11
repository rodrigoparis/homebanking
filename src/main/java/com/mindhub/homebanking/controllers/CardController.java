package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Card;
import org.springframework.http.HttpStatus;
import com.mindhub.homebanking.models.Client;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.mindhub.homebanking.services.implement.CardServiceImpl;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CardServiceImpl cardServiceImpl;

    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> createCard(@RequestParam String cardColor, @RequestParam String cardType, Authentication auth) {
        //Chequeo que no esten vacio o nulos
        if (cardColor.isBlank() || cardType.isBlank() || cardType == null || cardColor == null){
            return new ResponseEntity<>("No parameter can be null or empty", HttpStatus.FORBIDDEN);
        }

        Client client = clientRepository.findByEmail(auth.getName()).orElse(null);

        if (client != null) {
            boolean created = cardServiceImpl.createCard(cardColor, cardType, client);
            if (created) {
                return new ResponseEntity<>("New Card created", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Card limit reached for type " + cardType, HttpStatus.FORBIDDEN);
            }
        }

        return new ResponseEntity<>("client not found", HttpStatus.FORBIDDEN);


    }

    @PostMapping("/cards/delete")
    public ResponseEntity<String> logicCardDeletion(HttpServletResponse response, Authentication auth, String cardNumber) throws IOException {
        Client client = clientRepository.findByEmail(auth.getName()).orElse(null);
        Card card = cardRepository.findByNumber(cardNumber).orElse(null);

        if (!client.getCards().contains(card)) {
            return new ResponseEntity<String>("Account doesn't belong to authenticated client", HttpStatus.FORBIDDEN);
        }

        if (cardServiceImpl.deleteCard(card)){
            return new ResponseEntity<String>("Deletion succeed, please wait confirmation from our agents", HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<String>("something wrong happened, please contact our Service Office", HttpStatus.BAD_REQUEST);



    }
}
