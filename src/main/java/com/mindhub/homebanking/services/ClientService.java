package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.requestModels.RegistrationRequest;


public interface ClientService {

    boolean createClient(RegistrationRequest registrationRequest);
    int enableClient(String email);
}
