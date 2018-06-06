package pro.taskana.rest.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import pro.taskana.TaskQuery;
import pro.taskana.TaskService;
import pro.taskana.TaskState;
import pro.taskana.TaskSummary;
import pro.taskana.exceptions.InvalidArgumentException;

/**
 * TODO.
 */
@Component
public class TaskFilter {

    private static final String CLASSIFICATION = "classification";
    private static final String POR = "por";
    private static final String DOT = ".";

    private static final String STATE = "state";
    private static final String PRIORITY = "priority";
    private static final String DESCRIPTION = "description";
    private static final String NAME = "name";
    private static final String OWNER = "owner";
    private static final String WORKBASKET_ID = "workbasketId";
    private static final String CUSTOM = "custom";
    private static final String IS_TRANSFERRED = "isTransferred";
    private static final String IS_READ = "isRead";

    private static final String CLASSIFICATION_KEY = CLASSIFICATION + DOT + "key";

    private static final String POR_VALUE = POR + DOT + "value";
    private static final String POR_TYPE = POR + DOT + "type";
    private static final String POR_SYSTEM_INSTANCE = POR + DOT + "systemInstance";
    private static final String POR_SYSTEM = POR + DOT + "system";
    private static final String POR_COMPANY = POR + DOT + "company";

    private static final String CLAIMED = "CLAIMED";
    private static final String COMPLETED = "COMPLETED";
    private static final String READY = "READY";
    private static final String COMMA = ",";

    @Autowired
    private TaskService taskService;

    public List<TaskSummary> getAll() {
        return taskService.createTaskQuery().list();
    }

    public List<TaskSummary> inspectParams(MultiValueMap<String, String> params)
        throws InvalidArgumentException {
        TaskQuery taskQuery = taskService.createTaskQuery();

        // apply filters
        if (params.containsKey(NAME)) {
            String[] names = extractCommaSeperatedFields(params.get(NAME));
            taskQuery.nameIn(names);
        }
        if (params.containsKey(DESCRIPTION)) {
            taskQuery.descriptionLike(params.get(DESCRIPTION).get(0));
        }
        if (params.containsKey(PRIORITY)) {
            String[] prioritesInString = extractCommaSeperatedFields(params.get(PRIORITY));
            int[] priorites = extractPriorities(prioritesInString);
            taskQuery.priorityIn(priorites);
        }
        if (params.containsKey(STATE)) {
            TaskState[] states = extractStates(params);
            taskQuery.stateIn(states);
        }
        if (params.containsKey(CLASSIFICATION_KEY)) {
            String[] classificationKeys = extractCommaSeperatedFields(params.get(CLASSIFICATION_KEY));
            taskQuery.classificationKeyIn(classificationKeys);
        }
        if (params.containsKey(WORKBASKET_ID)) {
            String[] workbaskets = extractCommaSeperatedFields(params.get(WORKBASKET_ID));
            taskQuery.workbasketIdIn(workbaskets);
        }
        if (params.containsKey(OWNER)) {
            String[] owners = extractCommaSeperatedFields(params.get(OWNER));
            taskQuery.ownerIn(owners);
        }
        // objectReference
        if (params.keySet().stream().filter(s -> s.startsWith(POR)).toArray().length > 0) {
            if (params.containsKey(POR_COMPANY)) {
                String[] companies = extractCommaSeperatedFields(params.get(POR_COMPANY));
                taskQuery.primaryObjectReferenceCompanyIn(companies);
            }
            if (params.containsKey(POR_SYSTEM)) {
                String[] systems = extractCommaSeperatedFields(params.get(POR_SYSTEM));
                taskQuery.primaryObjectReferenceSystemIn(systems);
            }
            if (params.containsKey(POR_SYSTEM_INSTANCE)) {
                String[] systemInstances = extractCommaSeperatedFields(params.get(POR_SYSTEM_INSTANCE));
                taskQuery.primaryObjectReferenceSystemInstanceIn(systemInstances);
            }
            if (params.containsKey(POR_TYPE)) {
                String[] types = extractCommaSeperatedFields(params.get(POR_TYPE));
                taskQuery.primaryObjectReferenceTypeIn(types);
            }
            if (params.containsKey(POR_VALUE)) {
                String[] values = extractCommaSeperatedFields(params.get(POR_VALUE));
                taskQuery.primaryObjectReferenceValueIn(values);
            }
        }
        if (params.containsKey(IS_READ)) {
            taskQuery.readEquals(Boolean.getBoolean(params.get(IS_READ).get(0)));
        }
        if (params.containsKey(IS_TRANSFERRED)) {
            taskQuery.transferredEquals(Boolean.getBoolean(params.get(IS_TRANSFERRED).get(0)));
        }
        return taskQuery.list();
    }

    private int[] extractPriorities(String[] prioritesInString) {
        int[] priorites = new int[prioritesInString.length];
        for (int i = 0; i < prioritesInString.length; i++) {
            priorites[i] = Integer.getInteger(prioritesInString[i]);
        }
        return priorites;
    }

    private String[] extractCommaSeperatedFields(List<String> list) {
        List<String> values = new ArrayList<>();
        list.forEach(item -> values.addAll(Arrays.asList(item.split(COMMA))));
        return values.toArray(new String[0]);
    }

    private TaskState[] extractStates(MultiValueMap<String, String> params) throws InvalidArgumentException {
        List<TaskState> states = new ArrayList<>();
        for (String item : params.get(STATE)) {
            for (String state : item.split(COMMA)) {
                switch (state) {
                    case READY:
                        states.add(TaskState.READY);
                        break;
                    case COMPLETED:
                        states.add(TaskState.COMPLETED);
                        break;
                    case CLAIMED:
                        states.add(TaskState.CLAIMED);
                        break;
                    default:
                        throw new InvalidArgumentException("Unknown status '" + state + "'");
                }
            }
        }
        return states.toArray(new TaskState[0]);
    }
}
