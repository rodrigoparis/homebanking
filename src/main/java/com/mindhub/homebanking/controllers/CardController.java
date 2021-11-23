package com.mindhub.homebanking.controllers;

import org.springframework.http.HttpStatus;
import com.mindhub.homebanking.models.Client;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.mindhub.homebanking.services.implement.CardServiceImpl;

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
}
