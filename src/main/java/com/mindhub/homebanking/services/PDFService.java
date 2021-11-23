package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface PDFService {
    void generateAccountResume(HttpServletResponse response, Client client, Account account) throws IOException;
}
