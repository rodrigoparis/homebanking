package com.mindhub.homebanking.utils;

import org.jetbrains.annotations.NotNull;

public final class CardUtils {
    @NotNull
    public static String getCvv() {
        return ((Integer) (int) ((Math.random() * (999 - 100)) + 100)).toString();
    }

    @NotNull
    public static String getCardNumber() {
        String cardNumber = "";
        Integer randomNumber;
        for (int i = 0; i < 4; i++) {
            randomNumber = (int) ((Math.random() * (9999 - 1000)) + 1000);
            cardNumber = cardNumber.concat(randomNumber.toString());
            if (i != 3) {
                cardNumber = cardNumber.concat("-");
            }
        }
        return cardNumber;
    }
}
