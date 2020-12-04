package acceptance.objectreference;

import static org.assertj.core.api.Assertions.assertThat;

import helper.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.task.api.ObjectReferenceQuery;
import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.ObjectReference;

/** Acceptance test for all "query classifications with pagination" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryObjectreferencesWithPaginationAccTest extends AbstractAccTest {

  private TaskService taskService;
  private TaskQuery taskQuery;
  private ObjectReferenceQuery objRefQuery;

  QueryObjectreferencesWithPaginationAccTest() {
    super();
  }

  @BeforeEach
  void before() {
    taskService = taskanaEngine.getTaskService();
    taskQuery = taskService.createTaskQuery();
    objRefQuery = taskQuery.createObjectReferenceQuery();
  }

  @Test
  void testGetFirstPageOfObjectRefQueryWithOffset() {

    List<ObjectReference> results = objRefQuery.list(0, 5);
    assertThat(results).hasSize(3);
  }

  @Test
  void testGetSecondPageOfObjectRefQueryWithOffset() {
    List<ObjectReference> results = objRefQuery.list(2, 5);
    assertThat(results).hasSize(1);
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
    assertThat(results).hasSize(3);

    // Getting full page
    pageNumber = 2;
    pageSize = 2;
    results = objRefQuery.listPage(pageNumber, pageSize);
    assertThat(results).hasSize(1);

    // Getting last results on 1 big page
    pageNumber = 1;
    pageSize = 100;
    results = objRefQuery.listPage(pageNumber, pageSize);
    assertThat(results).hasSize(3);

    // Getting last results on multiple pages
    pageNumber = 2;
    pageSize = 2;
    results = objRefQuery.listPage(pageNumber, pageSize);
    assertThat(results).hasSize(1);
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
    assertThat(results).hasSize(3);
  }

  @Test
  void testCountOfClassificationsQuery() {
    long count = objRefQuery.count();
    assertThat(count).isEqualTo(3L);
  }
}
