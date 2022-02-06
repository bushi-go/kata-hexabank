package dev.rgoussu.hexabank.cli.adapters.persistence;

import java.io.File;

/**
 * Interface defining the operations (read an save) for the File based store.
 */
public interface FileAccountStore {
  void readFromFile(File accountFile);

  void saveToFile(File accountFile);
}
