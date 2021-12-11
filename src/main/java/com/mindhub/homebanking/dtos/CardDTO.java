package com.mindhub.homebanking.dtos;

import lombok.Data;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.enums.CardType;
import com.mindhub.homebanking.enums.CardColor;


@Data
@NoArgsConstructor
public class CardDTO {
    private Long id;
    private String number;
    private String cardHolder;
    private LocalDateTime fromDate;
    private LocalDateTime thruDate;
    private CardType type;
    private CardColor color;
    private String cvv;
    private String asociatedAccount;

    public CardDTO(Card card) {
        this.id = card.getId();;
        this.number = card.getNumber();
        this.cardHolder = card.getCardHolder();
        this.fromDate = card.getFromDate();
        this.thruDate = card.getThruDate();
        this.type = card.getType();
        this.color = card.getColor();
        this.cvv = card.getCvv();
        this.asociatedAccount = card.getAccountNumber();
    }


}
