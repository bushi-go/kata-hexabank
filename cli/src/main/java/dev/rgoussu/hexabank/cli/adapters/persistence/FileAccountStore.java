package dev.rgoussu.hexabank.cli.adapters.persistence;

import java.io.File;
import java.io.IOException;

/**
 * Interface defining the operations (read an save) for the File based store.
 */
public interface FileAccountStore {
  File getBackingFile() throws IOException;

  default void readFromFile() throws IOException {
    readFromFile(getBackingFile());
  }

  void readFromFile(File accountFile);

  default void saveToFile() throws IOException {
    saveToFile(getBackingFile());
  }

  void saveToFile(File accountFile);
}
