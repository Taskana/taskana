package pro.taskana;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.TaskanaEngine;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketType;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.util.IdGenerator;

/**
 * Rest Controller.
 */
@RestController
public class TaskanaTestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TaskanaEngine taskanaEngine;

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/schema")
    public @ResponseBody String schema() {
        String schema = jdbcTemplate.queryForObject("SELECT SCHEMA()", String.class);
        System.err.println("current schema: " + schema);
        return schema;
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @RequestMapping("/workbaskets")
    public @ResponseBody Integer workbaskets() {
        return getWorkbaskets();
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @RequestMapping("/customdb-tests")
    public @ResponseBody Integer customdbTests() {
        return getCustomdbTests();
    }

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/transaction")
    public @ResponseBody String transaction(@RequestParam(value = "rollback", defaultValue = "false") String rollback)
        throws InvalidWorkbasketException, NotAuthorizedException,
        WorkbasketAlreadyExistException, DomainNotFoundException {
        taskanaEngine.getWorkbasketService().createWorkbasket(createWorkBasket("key", "workbasket"));

        int workbaskets = getWorkbaskets();
        if (Boolean.valueOf(rollback)) {
            throw new RuntimeException("workbaskets: " + workbaskets);
        } else {
            return "workbaskets: " + workbaskets;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/transaction-many")
    public @ResponseBody String transactionMany(
        @RequestParam(value = "rollback", defaultValue = "false") String rollback)
        throws InvalidWorkbasketException, NotAuthorizedException,
        WorkbasketAlreadyExistException, DomainNotFoundException {
        taskanaEngine.getWorkbasketService().createWorkbasket(createWorkBasket("key1", "workbasket1"));
        taskanaEngine.getWorkbasketService().createWorkbasket(createWorkBasket("key2", "workbasket2"));
        taskanaEngine.getWorkbasketService().createWorkbasket(createWorkBasket("key3", "workbasket3"));

        if (Boolean.valueOf(rollback)) {
            throw new RuntimeException("workbaskets: " + getWorkbaskets());
        } else {
            return "workbaskets: " + getWorkbaskets();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/customdb")
    public @ResponseBody String transactionCustomdb(
        @RequestParam(value = "rollback", defaultValue = "false") String rollback)
        throws InvalidWorkbasketException, NotAuthorizedException,
        WorkbasketAlreadyExistException, DomainNotFoundException {
        taskanaEngine.getWorkbasketService().createWorkbasket(createWorkBasket("key1", "workbasket1"));
        taskanaEngine.getWorkbasketService().createWorkbasket(createWorkBasket("key2", "workbasket2"));

        jdbcTemplate.execute("INSERT INTO CUSTOMDB.TEST VALUES ('1', 'test')");
        jdbcTemplate.execute("INSERT INTO CUSTOMDB.TEST VALUES ('2', 'test2')");

        if (Boolean.valueOf(rollback)) {
            throw new RuntimeException("workbaskets: " + getWorkbaskets() + ", tests: " + getCustomdbTests());
        } else {
            return "workbaskets: " + getWorkbaskets() + ", tests: " + getCustomdbTests();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/cleanup")
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
        WorkbasketImpl workbasket = (WorkbasketImpl) taskanaEngine.getWorkbasketService().newWorkbasket(key,
            "DOMAIN_A");
        String id1 = IdGenerator.generateWithPrefix("TWB");
        workbasket.setId(id1);
        workbasket.setName(name);
        workbasket.setType(WorkbasketType.GROUP);
        return workbasket;
    }
}
