package com.mindhub.homebanking.models;

import com.mindhub.homebanking.enums.CardColor;
import com.mindhub.homebanking.enums.CardType;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.jetbrains.annotations.ApiStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private String number;
    private String cardHolder;
    private LocalDateTime fromDate;
    private LocalDateTime thruDate;
    private CardType type;
    private CardColor color;
    private String cvv;
    private Boolean enabled;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="client_id")
    private Client client;

    public Card(String number, LocalDateTime fromDate, LocalDateTime thruDate, CardType type, CardColor color, String cvv, Client client) {
        this.number = number;
        this.fromDate = fromDate;
        this.thruDate = thruDate;
        this.type = type;
        this.color = color;
        this.cvv = cvv;
        this.cardHolder = client.getLast_name() + " " +client.getFirst_name();
        this.client = client;
        this.enabled = true;
    }

    public void setCardHolder() {
        this.cardHolder = client.getLast_name() + " " +client.getFirst_name();
    }


}
