package io.kadai.example;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.exceptions.DomainNotFoundException;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.internal.util.IdGenerator;
import io.kadai.workbasket.api.WorkbasketType;
import io.kadai.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import io.kadai.workbasket.api.models.Workbasket;
import io.kadai.workbasket.internal.models.WorkbasketImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/** Rest Controller. */
@RestController
public class KadaiTestController {

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private KadaiEngine kadaiEngine;

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
      throws InvalidArgumentException,
          WorkbasketAlreadyExistException,
          DomainNotFoundException,
          NotAuthorizedException {
    kadaiEngine.getWorkbasketService().createWorkbasket(createWorkBasket("key", "workbasket"));

    Integer workbaskets = getWorkbaskets();
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
      throws InvalidArgumentException,
          WorkbasketAlreadyExistException,
          DomainNotFoundException,
          NotAuthorizedException {
    kadaiEngine.getWorkbasketService().createWorkbasket(createWorkBasket("key1", "workbasket1"));
    kadaiEngine.getWorkbasketService().createWorkbasket(createWorkBasket("key2", "workbasket2"));
    kadaiEngine.getWorkbasketService().createWorkbasket(createWorkBasket("key3", "workbasket3"));

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
      throws InvalidArgumentException,
          WorkbasketAlreadyExistException,
          DomainNotFoundException,
          NotAuthorizedException {
    kadaiEngine.getWorkbasketService().createWorkbasket(createWorkBasket("key1", "workbasket1"));
    kadaiEngine.getWorkbasketService().createWorkbasket(createWorkBasket("key2", "workbasket2"));

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

  private Integer getWorkbaskets() {
    // return kadaiEngine.getWorkbasketService().getWorkbaskets().size();
    return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WORKBASKET", Integer.class);
  }

  private Integer getCustomdbTests() {
    return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM CUSTOMDB.TEST", Integer.class);
  }

  private Workbasket createWorkBasket(String key, String name) {
    WorkbasketImpl workbasket =
        (WorkbasketImpl) kadaiEngine.getWorkbasketService().newWorkbasket(key, "DOMAIN_A");
    String id1 = IdGenerator.generateWithPrefix("TWB");
    workbasket.setId(id1);
    workbasket.setName(name);
    workbasket.setType(WorkbasketType.GROUP);
    return workbasket;
  }
}
