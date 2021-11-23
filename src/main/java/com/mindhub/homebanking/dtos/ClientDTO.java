package com.mindhub.homebanking.dtos;

import java.util.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.stream.Collectors;
import com.mindhub.homebanking.models.Client;


@Data
@NoArgsConstructor
public class ClientDTO {

    private Long id;
    private String email;
    private String first_name;
    private String last_name;
    private Set<AccountDTO> accounts = new HashSet();
    private Map<String,String> agenda = new HashMap<>();
    private Set<CardDTO> cards = new HashSet();
    private Set<ClientLoanDTO> clientLoans = new HashSet();

    public ClientDTO(Client client) {
        this.id = client.getId();
        this.first_name = client.getFirst_name();
        this.last_name = client.getLast_name();
        this.email = client.getEmail();
        this.accounts = client.getAccounts().stream().map(AccountDTO::new).collect(Collectors.toSet());
        this.clientLoans = client.getClientLoans().stream().map(ClientLoanDTO::new).collect(Collectors.toSet());
        this.cards = client.getCards().stream().map(CardDTO::new).collect(Collectors.toSet());
        this.agenda = client.getAgenda();
    }

}
