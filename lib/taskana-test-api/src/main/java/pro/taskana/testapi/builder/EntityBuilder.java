package pro.taskana.testapi.builder;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.security.auth.Subject;
import pro.taskana.common.api.security.UserPrincipal;

public interface EntityBuilder<EntityT, ServiceT> {

  EntityT buildAndStore(ServiceT service) throws Exception;

  default EntityT buildAndStore(ServiceT service, String userId) throws Exception {
    return execAsUser(userId, () -> buildAndStore(service));
  }

  private <T> T execAsUser(String userId, PrivilegedExceptionAction<T> runnable)
      throws PrivilegedActionException {
    Subject subject = new Subject();
    subject.getPrincipals().add(new UserPrincipal(userId));

    return Subject.doAs(subject, runnable);
  }

  interface SummaryEntityBuilder<SummaryEntityT, EntityT extends SummaryEntityT, ServiceT>
      extends EntityBuilder<EntityT, ServiceT> {
    SummaryEntityT entityToSummary(EntityT entity);

    default SummaryEntityT buildAndStoreAsSummary(ServiceT service) throws Exception {
      return entityToSummary(buildAndStore(service));
    }

    default SummaryEntityT buildAndStoreAsSummary(ServiceT service, String userId)
        throws Exception {
      return entityToSummary(buildAndStore(service, userId));
    }
  }
}
