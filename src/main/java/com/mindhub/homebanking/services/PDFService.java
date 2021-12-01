package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public interface PDFService {
    void generatePDF(HttpServletResponse response, Client client, String accountNumber, Set<Transaction> transactions) throws IOException;
}
