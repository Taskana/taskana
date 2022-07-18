package pro.taskana.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;

/** The TaskanaTestController is a Rest Controller. */
@RestController
public class TaskanaTestController {

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private TaskanaEngine taskanaEngine;

  @Transactional(rollbackFor = Exception.class)
  @GetMapping(path = "/schema")
  public @ResponseBody String schema() {
    String schema = jdbcTemplate.queryForObject("SELECT SCHEMA()", String.class);
    System.err.println("current schema: " + schema);
    return schema;
  }

  @Transactional(readOnly = true, rollbackFor = Exception.class)
  @GetMapping(path = "/workbaskets")
  public @ResponseBody Integer workbaskets() {
    return getWorkbaskets();
  }

  @Transactional(readOnly = true, rollbackFor = Exception.class)
  @GetMapping(path = "/customdb-tests")
  public @ResponseBody Integer customdbTests() {
    return getCustomdbTests();
  }

  @Transactional(rollbackFor = Exception.class)
  @GetMapping(path = "/transaction")
  public @ResponseBody String transaction(
      @RequestParam(value = "rollback", defaultValue = "false") String rollback)
      throws InvalidArgumentException, NotAuthorizedException, WorkbasketAlreadyExistException,
          DomainNotFoundException {
    taskanaEngine.getWorkbasketService().createWorkbasket(createWorkBasket("key", "workbasket"));

    int workbaskets = getWorkbaskets();
    if (Boolean.parseBoolean(rollback)) {
      throw new RuntimeException();
    } else {
      return "workbaskets: " + workbaskets;
    }
  }

  @Transactional(rollbackFor = Exception.class)
  @GetMapping(path = "/transaction-many")
  public @ResponseBody String transactionMany(
      @RequestParam(value = "rollback", defaultValue = "false") String rollback)
      throws InvalidArgumentException, NotAuthorizedException, WorkbasketAlreadyExistException,
          DomainNotFoundException {
    taskanaEngine.getWorkbasketService().createWorkbasket(createWorkBasket("key1", "workbasket1"));
    taskanaEngine.getWorkbasketService().createWorkbasket(createWorkBasket("key2", "workbasket2"));
    taskanaEngine.getWorkbasketService().createWorkbasket(createWorkBasket("key3", "workbasket3"));

    if (Boolean.parseBoolean(rollback)) {
      throw new RuntimeException();
    } else {
      return "workbaskets: " + getWorkbaskets();
    }
  }

  @Transactional(rollbackFor = Exception.class)
  @GetMapping(path = "/customdb")
  public @ResponseBody String transactionCustomdb(
      @RequestParam(value = "rollback", defaultValue = "false") String rollback)
      throws InvalidArgumentException, NotAuthorizedException, WorkbasketAlreadyExistException,
          DomainNotFoundException {
    taskanaEngine.getWorkbasketService().createWorkbasket(createWorkBasket("key1", "workbasket1"));
    taskanaEngine.getWorkbasketService().createWorkbasket(createWorkBasket("key2", "workbasket2"));

    jdbcTemplate.execute("INSERT INTO CUSTOMDB.TEST VALUES ('1', 'test')");
    jdbcTemplate.execute("INSERT INTO CUSTOMDB.TEST VALUES ('2', 'test2')");

    if (Boolean.parseBoolean(rollback)) {
      throw new RuntimeException();
    } else {
      return "workbaskets: " + getWorkbaskets() + ", tests: " + getCustomdbTests();
    }
  }

  @Transactional(rollbackFor = Exception.class)
  @DeleteMapping(path = "/cleanup")
  public @ResponseBody String cleanup() {
    jdbcTemplate.execute("DELETE FROM WORKBASKET");
    jdbcTemplate.execute("DELETE FROM CUSTOMDB.TEST");
    System.err.println("cleaned workbasket and test tables");
    return "cleaned workbasket and test tables";
  }

  private int getWorkbaskets() {
    // return taskanaEngine.getWorkbasketService().getWorkbaskets().size();
    return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WORKBASKET", Integer.class);
  }

  private int getCustomdbTests() {
    return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM CUSTOMDB.TEST", Integer.class);
  }

  private Workbasket createWorkBasket(String key, String name) {
    WorkbasketImpl workbasket =
        (WorkbasketImpl) taskanaEngine.getWorkbasketService().newWorkbasket(key, "DOMAIN_A");
    String id1 = IdGenerator.generateWithPrefix("TWB");
    workbasket.setId(id1);
    workbasket.setName(name);
    workbasket.setType(WorkbasketType.GROUP);
    return workbasket;
  }
}
