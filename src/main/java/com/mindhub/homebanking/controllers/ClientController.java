package com.mindhub.homebanking.controllers;


import java.util.Set;
import java.io.IOException;
import java.util.stream.Collectors;

import com.mindhub.homebanking.services.implement.ClientServiceImpl;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;

import com.mindhub.homebanking.dtos.ClientDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mindhub.homebanking.email.EmailValidator;
import org.springframework.security.core.Authentication;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.mindhub.homebanking.models.requestModels.RegistrationRequest;


@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientServiceImpl clientServiceImpl;


    private RegistrationRequest registrationRequest = new RegistrationRequest();

    @Autowired
    private EmailValidator emailValidator;

    @GetMapping("/clients")
    //Acá podria usar el @GetMapping("/clients")
    public Set<ClientDTO> getClients() {
        return clientRepository.findAll().stream().map(ClientDTO::new).collect(Collectors.toSet());
    }

    @GetMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id) {
        return clientRepository.findById(id).map(ClientDTO::new).orElse(null);
    }

    @PostMapping("/clients")
    public ResponseEntity<Object> createClient(
            @RequestParam String first_name, @RequestParam String last_name,
            @RequestParam String email, @RequestParam String password) {

        boolean isValidEmail = emailValidator.test(email);

        if (!isValidEmail) {
            return new ResponseEntity<>("Not valid email", HttpStatus.FORBIDDEN);
        }

        boolean clientExists = clientRepository.findByEmail(email).isPresent();

        if (clientExists) {
            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);
        }
        if (first_name.isBlank() || last_name.isBlank() || password.isBlank()) {
            return new ResponseEntity<>("Missing Data", HttpStatus.FORBIDDEN);
        }

        registrationRequest.setName(first_name);
        registrationRequest.setLast_name(last_name);
        registrationRequest.setPassword(password);
        registrationRequest.setEmail(email);

        if (clientServiceImpl.createClient(registrationRequest)) {
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>("Something wrong happened, please contact Support", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @GetMapping("/clients/current")
    public ClientDTO getClient(Authentication auth) {
        return new ClientDTO(clientRepository.findByEmail(auth.getName()).orElse(null));
    }

    @GetMapping("/clients/confirm")
    public ResponseEntity<Object> confirmClientEmail(@RequestParam("token") String token, HttpServletResponse resp) throws IOException {
        String response = clientServiceImpl.confirmClientEmail(token);

        if (response.equalsIgnoreCase("confirmed")) {
            resp.setStatus(200);
                resp.sendRedirect("/web/index.html");
            return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @PatchMapping("clients/modify")
    public void modifyClient(String name) {
        System.out.println(name);
    }

}
