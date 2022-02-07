package dev.rgoussu.hexabank.rest.adapters.endpoints.model.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dev.rgoussu.hexabank.core.operations.model.values.Money;
import java.io.IOException;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

/**
 * Simple serializer to normalize the way money is outputed from the api.
 */
@Component
public class MoneySerializer extends StdSerializer<Money> {
  protected MoneySerializer() {
    super(Money.class);
  }

  @Override
  public void serialize(Money money, JsonGenerator jsonGenerator,
                        SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeObjectField("amount", money.getAmount().setScale(2, RoundingMode.HALF_EVEN));
    jsonGenerator.writeObjectField("currency",
        money.getCurrency().getSymbol() + "(" + money.getCurrency().name() + ")");
    jsonGenerator.writeEndObject();
  }
}
