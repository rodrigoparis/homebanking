package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.enums.TransactionType;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import com.mindhub.homebanking.services.LoanService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LoanServiceImpl implements LoanService {

    @Autowired
    ClientLoanRepository clientLoanRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    LoanRepository loanRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    PayOutRepository payOutRepository;

    @Override
    public void createLoan(Double requestedAmount, Integer payments, String loanName, Client client, Account account, Integer interest) {
        ClientLoan clientLoan = new ClientLoan();
        Double amount = requestedAmount + (requestedAmount * interest/100);
        Double payment_amount = amount / payments;

        clientLoan.setAmount(Math.round(amount*100.0)/100.0);
        clientLoan.setRequestedAmount(Math.round(requestedAmount*100.0)/100.0);
        clientLoan.setTotalPayments(payments);
        clientLoan.setLoan(loanRepository.findByName(loanName).orElse(null));
        clientLoan.setClient(client);
        clientLoan.setRemainingPayments(payments);
        clientLoan.setPaymentAmount(payment_amount);
        clientLoanRepository.save(clientLoan);

        for (int i = 0; i < payments; i++) {
            PayOut lp = new PayOut(false, payment_amount, clientLoan);
            payOutRepository.save(lp);
            clientLoan.getPayOutController().add(lp);
        }

        Transaction transaction = new Transaction(
                TransactionType.CREDIT,
                requestedAmount,
                loanName + " Loan approved",
                LocalDateTime.now(),
                "MHB Loan Service",
                account.getNumber(),
                client.getName() + " " + client.getLast_name(),
                "MHBrothers"
        );

        Double currentDestinationBalance = account.getBalance() + requestedAmount;
        transaction.setCurrentBalance(currentDestinationBalance);

        account.addTransaction(transaction);
        account.setBalance(account.getBalance() + requestedAmount);
        accountRepository.save(account);
        transactionRepository.save(transaction);
    }

    @Override
    public void addLoan(LoanDTO newLoan) {
        Loan loan = new Loan();
        loan.setName(newLoan.getName());
        loan.setInterest(newLoan.getInterest());
        loan.setMaxAmount(newLoan.getMaxAmount());
        loan.setPayments(newLoan.getPayments());
        loan.setDescription(newLoan.getDescription());
        loanRepository.save(loan);

    }

    public List<LoanDTO> getLoans() {
        return loanRepository.findAll().stream().map(loan -> new LoanDTO(loan)).collect(Collectors.toList());
    }

    public String payOut(Long clientLoanID, String accountNumber, Integer quantity) {

        ClientLoan clientLoan = clientLoanRepository.findById(clientLoanID).orElse(null);
        Account account = accountRepository.findByNumber(accountNumber).orElse(null);
        Integer remainingPayOuts = clientLoan.getRemainingPayments();

        if (remainingPayOuts == 0) return "Loan completely cancelled";

        Double accountBalance = account.getBalance();
        Double totalDebt = clientLoan.getAmount();
        Double paymentAmount = clientLoan.getPaymentAmount();
        int availableQuantity = 0;

        while (accountBalance >= paymentAmount && availableQuantity != quantity) {
            accountBalance -= paymentAmount;
            availableQuantity++;
        }
        if (availableQuantity == 0) return "Lack of funds";
        paymentAmount *= availableQuantity;

        List<PayOut> payOutController =
                clientLoan
                        .getPayOutController()
                        .stream()
                        .filter(payOut -> payOut.getIsPayed() == false)
                        .collect(Collectors.toList());

        Iterator<PayOut> iterator =
                payOutController
                        .stream()
                        .iterator();
        int aux = availableQuantity;

        while (iterator.hasNext() && aux > 0) {
            PayOut toPay = iterator.next();
            clientLoan.setRemainingPayments(clientLoan.getRemainingPayments() - 1);
            toPay.setIsPayed(true);
            toPay.setPayOutDate(LocalDateTime.now());
            clientLoanRepository.save(clientLoan);
            aux--;
        }


        //Es posible pagar el monto de la cuota
        payOutTransaction(
                clientLoan.getPaymentAmount() * availableQuantity,
                clientLoan.getLoan().getName(),
                account,
                account.getClient().getFullName(),
                availableQuantity);
        if (availableQuantity != quantity) return "Sufficient funds for successful payment of " + availableQuantity + " installments";

        return "Payout successful for the amount of " + quantity+ " payments";
    }

    public void payOutTransaction(Double amount, String loanName, Account account, String clientName, Integer payOutQuantity) {
        Transaction transaction = new Transaction(
                TransactionType.DEBIT,
                -amount,
                loanName + " loan: payment of " + payOutQuantity + " installments",
                LocalDateTime.now(),
                account.getNumber(),
                "MHB Loan Service",
                "MHBrothers",
                clientName
        );

        Double currentOriginBalance = account.getBalance() - amount;
        transaction.setCurrentBalance(currentOriginBalance);
        account.addTransaction(transaction);
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);
        transactionRepository.save(transaction);
    }
}
