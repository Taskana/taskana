package acceptance.pojo;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoField;
import com.openpojo.validation.affirm.Affirm;
import com.openpojo.validation.rule.Rule;

// This Rule is copied from OpenPojo and was extended to excluded synthetic field entries
public class NoStaticExceptFinalRule implements Rule {

  public void evaluate(final PojoClass pojoClass) {
    for (PojoField fieldEntry : pojoClass.getPojoFields()) {
      if (!fieldEntry.isSynthetic() && fieldEntry.isStatic() && !fieldEntry.isFinal()) {
        Affirm.fail(
            String.format("Static fields=[%s] not marked final are not allowed", fieldEntry));
      }
    }
  }
}
