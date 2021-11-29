package com.mindhub.homebanking;

import com.mindhub.homebanking.enums.CardColor;
import com.mindhub.homebanking.enums.CardType;
import com.mindhub.homebanking.enums.UserRol;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import com.mindhub.homebanking.services.implement.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@SpringBootApplication
@ComponentScan("com.mindhub.homebanking")
public class HomebankingApplication {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountServiceImpl accountServiceImpl;

    @Autowired
    private CardServiceImpl cardServiceImpl;

    @Autowired
    private TransactionServiceImpl transactionServiceImpl;

    @Autowired
    private LoanServiceImpl loanServiceImpl;

    @Autowired
    private PDFServiceImpl pdf;

    public static void main(String[] args) {
        SpringApplication.run(HomebankingApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(ClientRepository clients,
                                      AccountRepository accounts,
                                      TransactionRepository transactions,
                                      LoanRepository loans,
                                      ClientLoanRepository clientLoans,
                                      CardRepository cards) {
        return (args) -> {

//prueba
            Client admin = new Client("admin", "admin", "admin@admin.com", passwordEncoder.encode("admin"), UserRol.ADMIN);
            admin.setIsEnabled(true);
            clients.save(admin);

            Client clientMelba = new Client("Melba", "Morel", "melba@mindhub.com", passwordEncoder.encode("melba"), UserRol.CLIENT);
            Account account = new Account("VIN-003", LocalDateTime.now().minusDays(5), 700D, clientMelba);
            Account account2 = new Account("VIN-004", LocalDateTime.now().plusDays(1), 250D, clientMelba);
            clientMelba.setIsEnabled(true);

            clients.save(clientMelba);
            accounts.save(account);
            accounts.save(account2);
            clients.save(clientMelba);

            //Creación Segundo Cliente
            Client clientRicardo = new Client("Ricardo", "Morel", "ricardo@mindhub.com", passwordEncoder.encode("ricardo"), UserRol.CLIENT);
            clientRicardo.setIsEnabled(true);

            //Creación Cuentas
            Account account3 = new Account("VIN-001", LocalDateTime.now(), 5000D, clientRicardo);
            Account account4 = new Account("VIN-002", LocalDateTime.now().plusDays(1), 7032D, clientRicardo);

            //Agregación Cuentas a Cliente
            clientRicardo.addAccount(account3);
            clientRicardo.addAccount(account4);

            //Guardado en repositorios
            clients.save(clientRicardo);
            accounts.save(account3);
            accounts.save(account4);

            String mortgageDescription = "A mortgage loan or simply mortgage is a loan used either by purchasers of real property to raise funds to buy real estate, or alternatively by existing property owners to raise funds for any purpose while putting a lien on the property being mortgaged.";
            String personalDescription = "A personal loan, as opposed to a commercial or business loan, is a loan to an individual for his or her own use. This type of loan is smaller than a mortgage and is typically used to purchase a car, renovate the home or pay for a vacation.";
            String autoDescription = "A car loan is a sum of money a consumer borrows in order to purchase a car. In most cases when purchasing a car, a borrower will specifically apply for a car loan; however, a consumer can also use a personal loan for the same purpose.";

            Loan hipoteca = new Loan("Mortgage", 500000D, List.of(12, 24, 36, 48, 60),35, mortgageDescription);
            Loan personal = new Loan("Personal", 100000D, List.of(6, 12, 24),30, personalDescription);
            Loan automotriz = new Loan("Auto", 300000D, List.of(6, 12, 24, 36),15,autoDescription);
            loans.save(automotriz);
            loans.save(hipoteca);
            loans.save(personal);

            //Prestamos Melba



            transactionServiceImpl.transactionTest("VIN-003", "VIN-004", 190.00, "Veggie Dinner with Friends", LocalDateTime.now().minusDays(7));
            transactionServiceImpl.transactionTest("VIN-004", "VIN-003", 255.00, "Booking Today", LocalDateTime.now().minusDays(6));
            transactionServiceImpl.transactionTest("VIN-003", "VIN-004", 300.00, "RyanAir - R0000534", LocalDateTime.now().minusDays(5));
            transactionServiceImpl.transactionTest("VIN-003", "VIN-004", 195.00, "BookStore - 5412", LocalDateTime.now().minusDays(4));
            transactionServiceImpl.transactionTest("VIN-004", "VIN-003", 210.00, "YouTube", LocalDateTime.now().minusDays(3));
            transactionServiceImpl.transactionTest("VIN-004", "VIN-003", 100.00, "School March Payment", LocalDateTime.now().minusDays(2));
            transactionServiceImpl.transactionTest("VIN-003", "VIN-004", 150.00, "That's all falks", LocalDateTime.now().minusDays(1));
            //Prestamos Ricardo
            ClientLoan ricardoLoan = new ClientLoan(100000D, personal.getPayments().get(2), clientRicardo, personal);
            clientLoans.save(ricardoLoan);

            ricardoLoan = new ClientLoan(100000D, automotriz.getPayments().get(3), clientRicardo, automotriz);
            clientLoans.save(ricardoLoan);

            Random random = new Random();

            String id = String.format("%04d", random.nextInt(10000));
            String id2 = String.format("%04d", random.nextInt(10000));
            Card melbaCard1 = new Card("4222 9867 " + id2 + " " + id, LocalDateTime.now(), LocalDateTime.now().plusYears(5), CardType.CREDIT, CardColor.GOLD, "255", clientMelba);

            id = String.format("%04d", random.nextInt(10000));
            id2 = String.format("%04d", random.nextInt(10000));
            Card melbaCard2 = new Card("4222 9867 " + id2 + " " + id, LocalDateTime.now(), LocalDateTime.now().plusYears(5), CardType.CREDIT, CardColor.TITANIUM, "006", clientMelba);

            id = String.format("%04d", random.nextInt(10000));
            id2 = String.format("%04d", random.nextInt(10000));
            Card melbaCard3 = new Card("4222 9867 " + id2 + " " + id, LocalDateTime.now(), LocalDateTime.now().plusYears(5), CardType.CREDIT, CardColor.SILVER, "897", clientMelba);

            cards.save(melbaCard1);
            cards.save(melbaCard2);
            cards.save(melbaCard3);
        };
    }
}
