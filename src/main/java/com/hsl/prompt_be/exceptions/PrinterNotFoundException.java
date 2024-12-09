package com.hsl.prompt_be.exceptions;

public class PrinterNotFoundException extends EntityNotFoundException {

    public PrinterNotFoundException () {
        super("PRINTER");
    }
}
