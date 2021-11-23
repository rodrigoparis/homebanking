package com.mindhub.homebanking.repositories;


import java.util.List;
import java.util.Optional;
import com.mindhub.homebanking.models.Client;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;



@RepositoryRestResource
public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findClientsByEmail(String email);
    Optional<Client> findByEmail(String email);


    @Transactional
    @Modifying
    @Query("UPDATE Client c " +
            "SET c.isEnabled = TRUE WHERE c.email = ?1")
    int enableClient(String email);


}
