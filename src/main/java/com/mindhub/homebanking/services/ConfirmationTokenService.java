package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.ConfirmationToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ConfirmationTokenService {

    void saveConfirmationToken(ConfirmationToken confirmationToken);

    Optional<ConfirmationToken> getConfirmationToken(String token);

    public int setTokenConfirmedAt(String token) ;

}
