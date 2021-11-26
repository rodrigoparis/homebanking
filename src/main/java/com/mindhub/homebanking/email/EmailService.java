package com.mindhub.homebanking.email;

public interface EmailService {

    void send(String to, String email);
    String createEmail(String name, String lastName, String link);
}
