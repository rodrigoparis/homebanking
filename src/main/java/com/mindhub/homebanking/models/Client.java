package com.mindhub.homebanking.models;


import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.stream.Collectors;
import com.mindhub.homebanking.enums.UserRol;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


@Entity
@Getter
@Setter
public class Client implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRol userRol;
    private Boolean isLogged = false;
    private Boolean isEnabled = false;

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private Set<Account> accounts = new HashSet();

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private Set<ClientLoan> clientLoans = new HashSet();

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private Set<Card> cards = new HashSet();

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private Set<ConfirmationToken> confirmationTokens = new HashSet();

    @ElementCollection
    @Column(name = "agenda")
    private Map<String, String> agenda = new HashMap<>();

    public Client() {
    }

    public Client(String firstName, String lastName, String email, String password, UserRol userRol) {
        this.first_name = firstName;
        this.last_name = lastName;
        this.email = email;
        this.password = password;
        this.userRol = userRol;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRol.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLogged;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    public List<Loan> getLoans() {
        return this.clientLoans.stream().map(ClientLoan::getLoan).collect(Collectors.toList());
    }


    public void addAccount(Account account) {
        account.setClient(this);
        accounts.add(account);
    }

    public void addClientLoan(ClientLoan clientLoan) {
        clientLoan.setClient(this);
        clientLoans.add(clientLoan);
    }

    public void addToAgenda(String accountNumber, String name) {
        this.agenda.put(accountNumber, name);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", userRol=" + userRol +
                ", isLogged=" + isLogged +
                ", isEnabled=" + isEnabled +
                ", clientLoans=" + clientLoans +
                ", cards=" + cards +
                ", confirmationTokens=" + confirmationTokens +
                ", agenda=" + agenda +
                '}';
    }

    public String getFullName() {
        return this.first_name + " " + this.last_name;
    }
}
