package dev.rgoussu.hexabank.business.exceptions;

public class NoSuchAccountException extends RuntimeException {
    public NoSuchAccountException(String s) {
        super(s);
    }
}
