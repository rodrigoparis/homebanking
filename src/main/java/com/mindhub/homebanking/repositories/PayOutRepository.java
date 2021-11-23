package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.PayOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;


@RepositoryRestResource
public interface PayOutRepository extends JpaRepository<PayOut, Long> {
    Optional<PayOut> findById(Long id);

}
