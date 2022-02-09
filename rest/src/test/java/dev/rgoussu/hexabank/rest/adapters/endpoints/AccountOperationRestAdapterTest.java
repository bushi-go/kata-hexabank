package dev.rgoussu.hexabank.rest.adapters.endpoints;


import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import dev.rgoussu.hexabank.core.history.ports.driving.AccountHistoryPort;
import dev.rgoussu.hexabank.core.operations.model.dto.OperationResult;
import dev.rgoussu.hexabank.core.operations.model.types.Currency;
import dev.rgoussu.hexabank.core.operations.model.types.OperationError;
import dev.rgoussu.hexabank.core.operations.model.types.OperationStatus;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import dev.rgoussu.hexabank.core.operations.services.AccountOperationService;
import dev.rgoussu.hexabank.rest.adapters.persistence.elastic.ElasticSearchAccountHistoryRepository;
import dev.rgoussu.hexabank.rest.adapters.persistence.mongo.MongoAccountPersistenceRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",
    "server.servlet.context-path=/api"})
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class,
    ElasticsearchDataAutoConfiguration.class})
@Import(AccountRestAdapter.class)
public class AccountOperationRestAdapterTest {

  @MockBean
  private MongoAccountPersistenceRepository repoBean;
  @MockBean
  private ElasticSearchAccountHistoryRepository esBean;
  @Autowired
  WebApplicationContext context;
  @LocalServerPort
  private int port;
  @MockBean
  private AccountOperationService accountOperationService;
  @MockBean
  private AccountHistoryPort accountHistoryPort;
  @BeforeEach
  public void setup() {
    RestAssured.port = port;
  }

  @Test
  public void givenValidDepositShouldReturn200WithNewBalance() throws Exception {
    String accountId = UUID.randomUUID().toString();
    when(accountOperationService.processDeposit(anyString(), any(Money.class)))
        .thenReturn(
            OperationResult.builder().accountId(accountId).status(OperationStatus.SUCCESS).error(
                OperationError.NONE).balance(Money.get(1010, Currency.EUR)).build());

    Response actual = given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .body("{\"amount\":10, \"currency\":\"EUR\"}")
        .put("/api/account/{accountId}/deposit", accountId);

    assertEquals(200, actual.statusCode());
    assertEquals(MediaType.APPLICATION_JSON_VALUE, actual.contentType());
    assertEquals(1010, ((Float) actual.jsonPath().get("balance.amount")).intValue());
  }

  @Test
  public void givenDepositAndExchangeRateNotAvailableShouldReturn503() throws Exception {
    String accountId = UUID.randomUUID().toString();
    when(accountOperationService.processDeposit(anyString(), any(Money.class)))
        .thenReturn(
            OperationResult.builder().accountId(accountId).status(OperationStatus.FAILURE).error(
                OperationError.COULD_NOT_CONVERT_TO_ACCOUNT_CURRENCY).build());

    Response actual = given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .body("{\"amount\":10, \"currency\":\"USD\"}")
        .put("/api/account/{accountId}/deposit", accountId);

    assertEquals(503, actual.statusCode());
  }

  @Test
  public void givenDepositAndAccoutNotFoundShouldReturn404() throws Exception {
    String accountId = UUID.randomUUID().toString();
    when(accountOperationService.processDeposit(anyString(), any(Money.class)))
        .thenReturn(
            OperationResult.builder().accountId(accountId).status(OperationStatus.FAILURE).error(
                OperationError.UNKNOWN_ACCOUNT).build());

    Response actual = given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .body("{\"amount\":10, \"currency\":\"USD\"}")
        .put("/api/account/{accountId}/deposit", accountId);

    assertEquals(404, actual.statusCode());
  }

  @Test
  public void givenInvalidDepositInputShouldReturn400() throws Exception {
    String accountId = UUID.randomUUID().toString();
    when(accountOperationService.processDeposit(anyString(), any(Money.class)))
        .thenReturn(
            OperationResult.builder().accountId(accountId).status(OperationStatus.FAILURE).error(
                OperationError.UNKNOWN_ACCOUNT).build());

    Response actual = given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .body("{\"amount\":10}")
        .put("/api/account/{accountId}/deposit", accountId);

    assertEquals(400, actual.statusCode());
  }

  @Test
  public void given0AmountDepositReturn400() throws Exception {
    String accountId = UUID.randomUUID().toString();
    when(accountOperationService.processDeposit(anyString(), any(Money.class)))
        .thenReturn(
            OperationResult.builder().accountId(accountId).status(OperationStatus.FAILURE).error(
                OperationError.UNKNOWN_ACCOUNT).build());

    Response actual = given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .body("{\"amount\":10}")
        .put("/api/account/{accountId}/deposit", accountId);

    assertEquals(400, actual.statusCode());
  }

  @Test
  public void givenDepositUncatchedExceptionShouldReturn500WithError() throws Exception {
    String accountId = UUID.randomUUID().toString();
    when(accountOperationService.processDeposit(anyString(), any(Money.class)))
        .thenThrow(new IllegalStateException("IllegalState"));

    Response actual = given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .body("{\"amount\":10, \"currency\": \"EUR\"}")
        .put("/api/account/{accountId}/deposit", accountId);

    assertEquals(500, actual.statusCode());
    assertEquals("IllegalState", actual.jsonPath().get("message"));
  }


  @Test
  public void givenValidWithdrawShouldReturn200WithNewBalance() throws Exception {
    String accountId = UUID.randomUUID().toString();
    when(accountOperationService.processWithdrawal(anyString(), any(Money.class)))
        .thenReturn(
            OperationResult.builder().accountId(accountId).status(OperationStatus.SUCCESS).error(
                OperationError.NONE).balance(Money.get(1010, Currency.EUR)).build());

    Response actual = given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .body("{\"amount\":10, \"currency\":\"EUR\"}")
        .put("/api/account/{accountId}/withdraw", accountId);

    assertEquals(200, actual.statusCode());
    assertEquals(MediaType.APPLICATION_JSON_VALUE, actual.contentType());
    assertEquals(1010, ((Float) actual.jsonPath().get("balance.amount")).intValue());
  }

  @Test
  public void givenWithdrawAndExchangeRateNotAvailableShouldReturn503() throws Exception {
    String accountId = UUID.randomUUID().toString();
    when(accountOperationService.processWithdrawal(anyString(), any(Money.class)))
        .thenReturn(
            OperationResult.builder().accountId(accountId).status(OperationStatus.FAILURE).error(
                OperationError.COULD_NOT_CONVERT_TO_ACCOUNT_CURRENCY).build());

    Response actual = given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .body("{\"amount\":10, \"currency\":\"USD\"}")
        .put("/api/account/{accountId}/withdraw", accountId);

    assertEquals(503, actual.statusCode());
  }

  @Test
  public void givenWithdrawAndAccoutNotFoundShouldReturn404() throws Exception {
    String accountId = UUID.randomUUID().toString();
    when(accountOperationService.processWithdrawal(anyString(), any(Money.class)))
        .thenReturn(
            OperationResult.builder().accountId(accountId).status(OperationStatus.FAILURE).error(
                OperationError.UNKNOWN_ACCOUNT).build());

    Response actual = given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .body("{\"amount\":10, \"currency\":\"USD\"}")
        .put("/api/account/{accountId}/withdraw", accountId);

    assertEquals(404, actual.statusCode());
  }

  @Test
  public void givenInvalidInputForWithdrawShouldReturn400() throws Exception {
    String accountId = UUID.randomUUID().toString();
    when(accountOperationService.processWithdrawal(anyString(), any(Money.class)))
        .thenReturn(
            OperationResult.builder().accountId(accountId).status(OperationStatus.FAILURE).error(
                OperationError.UNKNOWN_ACCOUNT).build());

    Response actual = given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .body("{\"amount\":10}")
        .put("/api/account/{accountId}/withdraw", accountId);

    assertEquals(400, actual.statusCode());
  }

  @Test
  public void given0AmountWithdrawReturn400() throws Exception {
    String accountId = UUID.randomUUID().toString();
    when(accountOperationService.processWithdrawal(anyString(), any(Money.class)))
        .thenReturn(
            OperationResult.builder().accountId(accountId).status(OperationStatus.FAILURE).error(
                OperationError.UNKNOWN_ACCOUNT).build());

    Response actual = given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .body("{\"amount\":10}")
        .put("/api/account/{accountId}/withdraw", accountId);

    assertEquals(400, actual.statusCode());
  }

  @Test
  public void givenUncatchedExceptionOnWithdrawShouldReturn500WithError() throws Exception {
    String accountId = UUID.randomUUID().toString();
    when(accountOperationService.processWithdrawal(anyString(), any(Money.class)))
        .thenThrow(new IllegalStateException("IllegalState"));

    Response actual = given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .body("{\"amount\":10, \"currency\": \"EUR\"}")
        .put("/api/account/{accountId}/withdraw", accountId);

    assertEquals(500, actual.statusCode());
    assertEquals("IllegalState", actual.jsonPath().get("message"));
  }
}
