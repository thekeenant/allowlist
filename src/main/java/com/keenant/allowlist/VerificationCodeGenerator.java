package com.keenant.allowlist;

import com.google.inject.Inject;
import java.util.Random;
import java.util.function.Supplier;

public class VerificationCodeGenerator implements Supplier<String> {

  private static final String SOURCES = "0123456789";
  private final Random random;

  @Inject
  public VerificationCodeGenerator(Random random) {
    this.random = random;
  }

  @Override
  public String get() {
    StringBuilder result = new StringBuilder();
    while (result.length() < 6) {
      result.append(SOURCES.charAt(random.nextInt(SOURCES.length())));
    }
    return result.toString();
  }
}
