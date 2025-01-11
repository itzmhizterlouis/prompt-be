package com.hsl.prompt_be.exceptions;

public class WalletBalanceIsZeroException extends PrinthubException {

    public WalletBalanceIsZeroException() {
        super("WALLET BALANCE IS ZERO");
    }
}
