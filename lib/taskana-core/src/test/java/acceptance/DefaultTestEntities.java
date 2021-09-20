package acceptance;

import java.util.UUID;

import pro.taskana.classification.internal.builder.ClassificationBuilder;
import pro.taskana.task.internal.builder.ObjectReferenceBuilder;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.internal.builder.WorkbasketBuilder;

public class DefaultTestEntities {

  public static ClassificationBuilder defaultTestClassification() {
    return ClassificationBuilder.newClassification()
        .key(UUID.randomUUID().toString().replace("-", ""))
        .domain("DOMAIN_A");
  }

  public static WorkbasketBuilder defaultTestWorkbasket() {
    return WorkbasketBuilder.newWorkbasket()
        .key(UUID.randomUUID().toString())
        .domain("DOMAIN_A")
        .name("Megabasket")
        .type(WorkbasketType.GROUP)
        .orgLevel1("company");
  }

  public static ObjectReferenceBuilder defaultTestObjectReference() {
    return ObjectReferenceBuilder.newObjectReference()
        .company("Company1")
        .system("System1")
        .systemInstance("Instance1")
        .type("Type1")
        .value("Value1");
  }
}
