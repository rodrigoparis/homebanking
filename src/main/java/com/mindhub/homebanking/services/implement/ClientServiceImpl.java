package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.email.EmailServiceImpl;
import com.mindhub.homebanking.enums.AccountType;
import com.mindhub.homebanking.enums.UserRol;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.ConfirmationToken;
import com.mindhub.homebanking.models.requestModels.RegistrationRequest;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.ConfirmationTokenRepository;
import com.mindhub.homebanking.services.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
@AllArgsConstructor
public class ClientServiceImpl implements UserDetailsService, ClientService {

    @Autowired
    private ClientRepository clientRepository;
    private final static String USER_NOT_FOUND_MSG = "User with email %s not found";
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private EmailServiceImpl emailServiceImpl;
    @Autowired
    private AccountServiceImpl accountServiceImpl;
    @Autowired
    private ConfirmationTokenServiceImpl confirmationTokenServiceImpl;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return clientRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }
    @Override
    public boolean createClient(RegistrationRequest request) {

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Client client = new Client(request.getName(), request.getLast_name(), request.getEmail(), encodedPassword, UserRol.CLIENT);

        clientRepository.save(client);
        accountServiceImpl.createAccount(client, AccountType.SAVINGS);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), client);
        confirmationTokenServiceImpl.saveConfirmationToken(confirmationToken);

        String link = "https://mh-homebanking.herokuapp.com/api/clients/confirm?token=" + token;
        String email = emailServiceImpl.createEmail(request.getName(),request.getLast_name(), link);
        emailServiceImpl.send(request.getEmail(), email);

        return true;

    }

    @Override
    public int enableClient(String email) {
        return clientRepository.enableClient(email);
    }


    @Transactional
    public String confirmClientEmail(String token) {

        ConfirmationToken confirmationToken = confirmationTokenServiceImpl.getConfirmationToken(token).orElse(null);
        if (confirmationToken == null) return "token not found";
        if (confirmationToken.getConfirmedAt() != null) return "email already confirmed";
        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) return "token expired";

        confirmationTokenServiceImpl.setTokenConfirmedAt(token);
        enableClient(confirmationToken.getClient().getEmail());
        return "confirmed";
    }
}
