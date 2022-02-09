package dev.rgoussu.hexabank.cli.adapters.persistence.model;

import dev.rgoussu.hexabank.core.history.model.values.AccountOperationSummary;
import dev.rgoussu.hexabank.core.operations.model.types.Currency;
import dev.rgoussu.hexabank.core.operations.model.types.OperationStatus;
import dev.rgoussu.hexabank.core.operations.model.types.OperationType;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public class CsvAccountOperationRecord implements Comparable<CsvAccountOperationRecord> {
  private static final String ACCOUNT_ID_HEADER = "ACCOUNT_ID";
  private static final String DATE_HEADER = "DATE";
  private static final String TYPE_HEADER = "TYPE";
  private static final String STATUS_HEADER = "STATUS";
  private static final String AMOUNT_HEADER = "AMOUNT";
  private static final String CURRENCY_AMOUNT_HEADER = "CURRENCY_AMOUNT";
  private static final String BALANCE_HEADER = "BALANCE";
  private static final String CURRENCY_BALANCE_HEADER = "CURRENCY_BALANCE";
  private static final String[] HEADERS =
      {ACCOUNT_ID_HEADER, DATE_HEADER, TYPE_HEADER, STATUS_HEADER, AMOUNT_HEADER,
          CURRENCY_AMOUNT_HEADER,
          BALANCE_HEADER, CURRENCY_BALANCE_HEADER};
  private static final String DEFAULT_DELIMITER = ",";
  private final String accountId;
  private final Instant date;
  private final OperationType operationType;
  private final OperationStatus operationStatus;
  private final Money operationAmount;
  private final Money balanceAfterOperation;

  private CsvAccountOperationRecord(String accountId, Instant date,
                                    OperationType operationType,
                                    OperationStatus operationStatus,
                                    Money operationAmount,
                                    Money balanceAfterOperation) {
    this.accountId = accountId;
    this.date = date;
    this.operationType = operationType;
    this.operationStatus = operationStatus;
    this.operationAmount = operationAmount;
    this.balanceAfterOperation = balanceAfterOperation;
  }


  /**
   * Static method to get the default header line for the given delimiter.
   *
   * @param delimiter the delimiter to user
   * @return the headerline joined by delimiter
   */
  public static String getHeaderLine(String delimiter) {
    return String.join(delimiter, HEADERS);
  }

  public static String getDefaultDelimiter() {
    return DEFAULT_DELIMITER;
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
  public static CsvAccountOperationRecord fromCsv(String csvLine, String delimiter,
                                                  String headerLine)
      throws IllegalArgumentException {
    List<String> headers = Stream.of(headerLine.split("[" + delimiter + "]")).toList();
    if (Stream.of(HEADERS).anyMatch(header -> !headers.contains(header))) {
      throw new IllegalArgumentException("Missing headers on csv " + headers);
    }
    String[] parts = csvLine.split("[" + delimiter + "]");
    if (parts.length != headers.size()) {
      throw new IllegalArgumentException("Malformed csv line - could not read account data");
    }
    try {
      String id = parts[headers.indexOf(ACCOUNT_ID_HEADER)];
      Instant date = Instant.parse(parts[headers.indexOf(DATE_HEADER)]);
      OperationType type = OperationType.valueOf(parts[headers.indexOf(TYPE_HEADER)]);
      OperationStatus status = OperationStatus.valueOf(parts[headers.indexOf(STATUS_HEADER)]);
      double amount = Double.parseDouble(parts[headers.indexOf(AMOUNT_HEADER)]);
      Currency amountCurrency = Currency.valueOf(parts[headers.indexOf(CURRENCY_AMOUNT_HEADER)]);
      double balance = Double.parseDouble(parts[headers.indexOf(BALANCE_HEADER)]);
      Currency balanceCurrency = Currency.valueOf(parts[headers.indexOf(CURRENCY_BALANCE_HEADER)]);
      return new CsvAccountOperationRecord(id, date, type, status,
          Money.get(amount, amountCurrency),
          Money.get(balance, balanceCurrency));
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not read csv data", e);
    }
  }

  public static CsvAccountOperationRecord fromOperationSummary(String accountId,
                                                               AccountOperationSummary operationSummary) {
    return CsvAccountOperationRecord.builder().accountId(accountId)
        .date(operationSummary.getOperationDate())
        .operationType(operationSummary.getOperationType())
        .operationStatus(operationSummary.getOperationStatus())
        .operationAmount(operationSummary.getOperationAmount())
        .balanceAfterOperation(operationSummary.getBalanceAfterOperation())
        .build();
  }

  public String toCsv(String delimiter, String headerLine) {
    return Stream.of(headerLine.split("[" + delimiter + "]")).map(header -> switch (header) {
      case ACCOUNT_ID_HEADER -> accountId;
      case DATE_HEADER -> date.toString();
      case TYPE_HEADER -> operationType.name();
      case STATUS_HEADER -> operationStatus.name();
      case AMOUNT_HEADER -> operationAmount.getAmount().toString();
      case CURRENCY_AMOUNT_HEADER -> operationAmount.getCurrency().name();
      case BALANCE_HEADER -> balanceAfterOperation.getAmount().toString();
      case CURRENCY_BALANCE_HEADER -> balanceAfterOperation.getCurrency().name();
      default -> null;
    }).filter(Objects::nonNull).collect(Collectors.joining(delimiter));
  }

  public AccountOperationSummary toSummary() {
    return AccountOperationSummary.builder()
        .operationDate(date)
        .operationStatus(operationStatus)
        .operationType(operationType)
        .operationAmount(operationAmount)
        .balanceAfterOperation(balanceAfterOperation)
        .build();
  }

  @Override
  public int compareTo(CsvAccountOperationRecord other) {
    return date.compareTo(other.getDate()) * -1;
  }
}
