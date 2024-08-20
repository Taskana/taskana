package io.kadai.testapi;

import static io.kadai.testapi.builder.ClassificationBuilder.newClassification;
import static io.kadai.testapi.builder.ObjectReferenceBuilder.newObjectReference;
import static io.kadai.testapi.builder.UserBuilder.newUser;
import static io.kadai.testapi.builder.WorkbasketBuilder.newWorkbasket;

import io.kadai.testapi.builder.ClassificationBuilder;
import io.kadai.testapi.builder.ObjectReferenceBuilder;
import io.kadai.testapi.builder.UserBuilder;
import io.kadai.testapi.builder.WorkbasketBuilder;
import io.kadai.workbasket.api.WorkbasketType;
import java.util.Random;
import java.util.UUID;

public class DefaultTestEntities {

  private DefaultTestEntities() {
    throw new IllegalStateException("Utility class");
  }

  public static ClassificationBuilder defaultTestClassification() {
    return newClassification()
        .key(UUID.randomUUID().toString().replace("-", ""))
        .domain("DOMAIN_A");
  }

  public static WorkbasketBuilder defaultTestWorkbasket() {
    return newWorkbasket()
        .key(UUID.randomUUID().toString())
        .domain("DOMAIN_A")
        .name("Megabasket")
        .type(WorkbasketType.GROUP)
        .orgLevel1("company");
  }

  public static ObjectReferenceBuilder defaultTestObjectReference() {
    return newObjectReference()
        .company("Company1")
        .system("System1")
        .systemInstance("Instance1")
        .type("Type1")
        .value("Value1");
  }

  public static UserBuilder randomTestUser() {
    return newUser()
        .id(UUID.randomUUID().toString().replace("-", ""))
        .firstName(RandomStringGenerator.generateRandomString(10))
        .lastName(RandomStringGenerator.generateRandomString(12));
  }

  private static class RandomStringGenerator {
    private static final Random RANDOM = new Random(15);

    private static String generateRandomString(int length) {
      // see ascii table for details number -> char conversion.
      return RANDOM
          .ints('0', 'z' + 1)
          .filter(i -> (i <= '9' || i >= 'A') && (i <= 'Z' || i >= 'a'))
          .limit(length)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          .toString();
    }
  }
}
