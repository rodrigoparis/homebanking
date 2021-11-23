package com.mindhub.homebanking.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import com.mindhub.homebanking.models.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.context.annotation.Configuration;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;


@Configuration
@ComponentScan("com.mindhub.homebanking.configuration")
public class WebAuthentication extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    ClientRepository clientRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(inputName -> {
            /*If client is not found on repository throws and exception using .orElseThrow with Supplier */
            Client client = clientRepository.findByEmail(inputName)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "User not found"));

            //Ordinal 0 for ENUM UserRol = ADMIN
            if (client.getUserRol().ordinal() == 0) {
                return new User(client.getEmail(), client.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList("ADMIN,CLIENT"));
            }
            //Client is enabled after confirmation of email only through unique token
            if (!client.isEnabled()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not validated");

            return new User(client.getEmail(), client.getPassword(), AuthorityUtils.createAuthorityList("CLIENT"));
        });
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
