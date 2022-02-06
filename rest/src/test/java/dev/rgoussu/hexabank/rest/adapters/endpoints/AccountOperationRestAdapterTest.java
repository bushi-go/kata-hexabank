package dev.rgoussu.hexabank.rest.adapters.endpoints;


import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.rgoussu.hexabank.core.model.dto.DepositResult;
import dev.rgoussu.hexabank.core.model.types.Currency;
import dev.rgoussu.hexabank.core.model.types.DepositError;
import dev.rgoussu.hexabank.core.model.types.DepositStatus;
import dev.rgoussu.hexabank.core.model.values.Money;
import dev.rgoussu.hexabank.core.services.AccountOperationService;
import dev.rgoussu.hexabank.rest.adapters.persistence.MongoAccountPersistenceRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
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
@EnableAutoConfiguration(exclude = MongoAutoConfiguration.class)
@Import(AccountOperationRestAdapter.class)
public class AccountOperationRestAdapterTest {

  @LocalServerPort
  private int port;

  @MockBean
  private AccountOperationService accountOperationService;
  @MockBean
  MongoAccountPersistenceRepository repoBean;

  @BeforeEach
  public void setup() {
    RestAssured.port = port;
  }

  @Autowired
  WebApplicationContext context;

  @Test
  public void givenValidDepositShouldReturn200WithNewBalance() throws Exception {
    String accountId = UUID.randomUUID().toString();
    when(accountOperationService.processDeposit(anyString(),any(Money.class)))
        .thenReturn(
            DepositResult.builder().accountId(accountId).status(DepositStatus.SUCCESS).error(
                DepositError.NONE).balance(Money.get(1010, Currency.EUR)).build());

    Response actual = given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .body("{\"amount\":10, \"currency\":\"EUR\"}")
        .put("/api/account/{accountId}/deposit", accountId);

    assertEquals(200, actual.statusCode());
    assertEquals(MediaType.APPLICATION_JSON_VALUE, actual.contentType());
    assertEquals(1010, ((Float) actual.jsonPath().get("balance.amount")).intValue());
  }
}
