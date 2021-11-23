package com.mindhub.homebanking.models;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "payout")
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class PayOut {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Getter
    @Setter
    private Long loanPaymentId;

    @NonNull
    @Getter
    @Setter
    private Boolean isPayed;
    @NonNull
    @Getter @Setter
    private Double paymentAmount;

    @Getter @Setter
    private LocalDateTime payOutDate;

    @ManyToOne
    @JoinColumn(name="clientLoan_id")
    @NonNull
    private ClientLoan clientLoan;

}
