package com.hsl.prompt_be.exceptions;

public class WalletBalanceException extends PrinthubException {

    public WalletBalanceException() {
        super("WALLET MONEY CANNOT BE WITHDRAWN BECAUSE IT IS LESS THAN N1000");
    }
}
