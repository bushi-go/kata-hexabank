package dev.rgoussu.hexabank.cli.adapters.persistence.model;

import dev.rgoussu.hexabank.core.operations.model.entities.Account;
import dev.rgoussu.hexabank.core.operations.model.types.Currency;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A simple CSV representation of an account.
 * Can be read from file with fields in any order, provided that the header line is provided
 * Will be written back to file in the same fashion.
 */
public record CsvAccountRecord(String accountId, Money balance) {
  private static final String ID_HEADER = "ID";
  private static final String BALANCE_HEADER = "BALANCE";
  private static final String CURRENCY_HEADER = "CURRENCY";
  private static final String[] HEADERS = {ID_HEADER, BALANCE_HEADER, CURRENCY_HEADER};
  private static final String DEFAULT_DELIMITER = ",";

  /**
   * Static method to get the default header line for the given delimiter.
   *
   * @param delimiter the delimiter to user
   * @return the headerline joined by delimiter
   */
  public static String getHeaderLine(String delimiter) {
    return String.join(delimiter, HEADERS);
  }


  /**
   * Method to check if line is a valid header.
   *
   * @param line line to check
   * @return true if the line is a header line, false otherwise
   */
  public static boolean isHeaderLine(String line) {
    return Arrays.stream(HEADERS).map(line::contains)
        .reduce(true, (acc, res) -> acc && res);
  }

  public static String getDefaultDelimiter() {
    return DEFAULT_DELIMITER;
  }

  /**
   * Read an Account record from a csv line, with the given delimiter and in the order dictated
   * by the header line.
   *
   * @param csvLine    the line to be read
   * @param delimiter  the delimiter used
   * @param headerLine the header line dicatating the order of fields in the line
   * @return the AccountRecord read
   * @throws IllegalArgumentException if for any reason the csv line could not be parsed
   */
  public static CsvAccountRecord fromCsv(String csvLine, String delimiter,
                                         String headerLine) throws IllegalArgumentException {
    List<String> headers = Stream.of(headerLine.split("[" + delimiter + "]")).toList();
    if (Stream.of(HEADERS).anyMatch(header -> !headers.contains(header))) {
      throw new IllegalArgumentException("Missing headers on csv " + headers);
    }
    String[] parts = csvLine.split("[" + delimiter + "]");
    if (parts.length != headers.size()) {
      throw new IllegalArgumentException("Malformed csv line - could not read account data");
    }
    try {
      String id = parts[headers.indexOf(ID_HEADER)];
      double balance = Double.parseDouble(parts[headers.indexOf(BALANCE_HEADER)]);
      Currency currency = Currency.valueOf(parts[headers.indexOf(CURRENCY_HEADER)]);
      return new CsvAccountRecord(id, Money.get(balance, currency));
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not read csv data", e);
    }
  }

  /**
   * Map an account to this csv representation.
   *
   * @param account account to generate from
   * @return a csv capable representation of the account
   */
  public static CsvAccountRecord fromAccount(Account account) {
    return new CsvAccountRecord(account.getAccountId(), account.getBalance());
  }

  /**
   * Write the record to a csv string representation with the given delimiter and following
   * the order dictated by the header line.
   *
   * @param delimiter  delimiter to use between field
   * @param headerLine header line of the target csv format
   * @return CSV String representation of the account;
   */
  public String toCsv(String delimiter, String headerLine) {

    return Stream.of(headerLine.split("[" + delimiter + "]")).map(header ->
      switch(header) {
        case ID_HEADER -> accountId;
        case CURRENCY_HEADER ->  balance.getCurrency().name();
        case BALANCE_HEADER ->  balance.getAmount().toString();
        default -> null;
      }
    ).filter(Objects::nonNull).collect(Collectors.joining(delimiter));
  }

  /**
   * Map this record instance to an account business object.
   *
   * @return the business object this record represents
   */
  public Account toAccount() {
    return Account.create(accountId, balance);
  }


}
