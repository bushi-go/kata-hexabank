package dev.rgoussu.hexabank.cli.adapters.endpoints;

import dev.rgoussu.hexabank.cli.operations.BankOperation;
import org.mockito.internal.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.Map;

@Component
public class CliDisplay {
    private static final int LINE_LENGTH = 100;
    private static final int EXIT_CODE = 0;

    public void clearScreen() {
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }

    public void print(String message){
        printLeft(message);
    }

    public void printRight(String message) {
        printLeftPadded(message, " ");
    }

    public void printLeft(String message){
        printRightPadded(message, " ");
    }

    public void printCentered(String message){
        printCentered(message, " ");
    }

    public void printRightAsSeparator(String message) {
        printRightPadded(message, "-");
    }
    public void printLeftAsSeparator(String message) {
        printLeftPadded(message, "-");
    }

    public void printCenteredAsSeparator(String message) {
        printCentered(message, "-");
    }

    public void printCentered(String message, String padding){
        System.out.println("| "+padMessageCenter(message, padding)+" |");
    }

    private void printRightPadded(String message, String padding) {
        System.out.println("|" + padMessageRight(message, padding) + "|");
    }

    private void printLeftPadded(String message,String padding){
        System.out.println("|" + padMessageLeft(message, padding) + "|");
    }

    private String padMessageRight(String message, String padding) {
        return String.format("%1$-" + (LINE_LENGTH - 4) + "s", message).replace(" ", padding);
    }
    private String padMessageLeft(String message, String padding) {
        return String.format("%1$" + (LINE_LENGTH - 4) + "s", message).replace(" ", padding);
    }
    private String padMessageCenter(String message, String padding) {
        int paddingSize = (LINE_LENGTH-4-message.length()-1)/2;
        return padding.repeat(paddingSize)+message+padding.repeat(paddingSize);
    }

}
