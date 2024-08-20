package acceptance.task.query;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.task.api.ObjectReferenceQuery;
import io.kadai.task.api.TaskQuery;
import io.kadai.task.api.models.ObjectReference;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/** Acceptance test for all "query classifications with pagination" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryObjectReferencesWithPaginationAccTest extends AbstractAccTest {

  private ObjectReferenceQuery objRefQuery;

  @BeforeEach
  void before() {
    TaskQuery taskQuery = kadaiEngine.getTaskService().createTaskQuery();
    objRefQuery = taskQuery.createObjectReferenceQuery();
  }

  @Test
  void testGetFirstPageOfObjectRefQueryWithOffset() {

    List<ObjectReference> results = objRefQuery.list(0, 5);
    assertThat(results).hasSize(4);
  }

  @Test
  void testGetSecondPageOfObjectRefQueryWithOffset() {
    List<ObjectReference> results = objRefQuery.list(2, 5);
    assertThat(results).hasSize(2);
  }

  @Test
  void testListOffsetAndLimitOutOfBounds() {
    // both will be 0, working
    List<ObjectReference> results = objRefQuery.list(-1, -3);
    assertThat(results).isEmpty();

    // limit will be 0
    results = objRefQuery.list(1, -3);
    assertThat(results).isEmpty();

    // offset will be 0
    results = objRefQuery.list(-1, 3);
    assertThat(results).hasSize(3);
  }

  @Test
  void testPaginationWithPages() {
    // Getting full page
    int pageNumber = 1;
    int pageSize = 10;
    List<ObjectReference> results = objRefQuery.listPage(pageNumber, pageSize);
    assertThat(results).hasSize(4);

    // Getting full page
    pageNumber = 2;
    pageSize = 2;
    results = objRefQuery.listPage(pageNumber, pageSize);
    assertThat(results).hasSize(2);

    // Getting last results on 1 big page
    pageNumber = 1;
    pageSize = 100;
    results = objRefQuery.listPage(pageNumber, pageSize);
    assertThat(results).hasSize(4);
  }

  @Test
  void testPaginationNullAndNegativeLimitsIgnoring() {
    // 0 limit/size = 0 results
    int pageNumber = 2;
    int pageSize = 0;
    List<ObjectReference> results = objRefQuery.listPage(pageNumber, pageSize);
    assertThat(results).isEmpty();

    // Negative will be 0 = all results
    pageNumber = 2;
    pageSize = -1;
    results = objRefQuery.listPage(pageNumber, pageSize);
    assertThat(results).isEmpty();

    // Negative page = first page
    pageNumber = -1;
    pageSize = 10;
    results = objRefQuery.listPage(pageNumber, pageSize);
    assertThat(results).hasSize(4);
  }

  @Test
  void testCountOfClassificationsQuery() {
    long count = objRefQuery.count();
    assertThat(count).isEqualTo(4L);
  }
}
