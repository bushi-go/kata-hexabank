package dev.rgoussu.hexabank.cli;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hexa bank Command Line Interface Application.
 */
@SpringBootApplication
public class HexaBankCliApplication implements CommandLineRunner {

  public static void main(String... args) {
    SpringApplication.run(HexaBankCliApplication.class, args);
  }

  @Override
  public void run(String... args) {

  }

}
