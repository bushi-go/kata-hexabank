package dev.rgoussu.hexabank.cli.adapters.persistence;

import java.io.File;

public interface FileAccountStore {
  void readFromFile(File accountFile);
  void saveToFile(File accountFile);
}
