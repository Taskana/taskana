package pro.taskana.rest.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import pro.taskana.ClassificationQuery;
import pro.taskana.ClassificationService;
import pro.taskana.ObjectReferenceQuery;
import pro.taskana.TaskQuery;
import pro.taskana.TaskService;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.model.Task;
import pro.taskana.model.TaskState;

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

    private static final String CLASSIFICATION_PARENT_KEY = CLASSIFICATION + DOT + "parentClassificationKey";
    private static final String CLASSIFICATION_CATEGORY = CLASSIFICATION + DOT + "category";
    private static final String CLASSIFICATION_TYPE = CLASSIFICATION + DOT + "type";
    private static final String CLASSIFICATION_NAME = CLASSIFICATION + DOT + "name";
    private static final String CLASSIFICATION_DESCRIPTION = CLASSIFICATION + DOT + "description";
    private static final String CLASSIFICATION_PRIORITY = CLASSIFICATION + DOT + "priority";
    private static final String CLASSIFICATION_SERVICE_LEVEL = CLASSIFICATION + DOT + "serviceLevel";

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

    @Autowired
    private ClassificationService classificationService;

    public List<Task> getAll() throws NotAuthorizedException {
        return taskService.createTaskQuery().list();
    }

    public List<Task> inspectPrams(MultiValueMap<String, String> params) throws NotAuthorizedException {
        TaskQuery taskQuery = taskService.createTaskQuery();

        // apply filters
        if (params.containsKey(NAME)) {
            String[] names = extractCommaSeperatedFields(params.get(NAME));
            taskQuery.name(names);
        }
        if (params.containsKey(DESCRIPTION)) {
            taskQuery.descriptionLike(params.get(DESCRIPTION).get(0));
        }
        if (params.containsKey(PRIORITY)) {
            String[] prioritesInString = extractCommaSeperatedFields(params.get(PRIORITY));
            int[] priorites = extractPriorities(prioritesInString);
            taskQuery.priority(priorites);
        }
        if (params.containsKey(STATE)) {
            TaskState[] states = extractStates(params);
            taskQuery.state(states);
        }
        // classification
        if (params.keySet().stream().filter(s -> s.startsWith(CLASSIFICATION)).toArray().length > 0) {
            ClassificationQuery classificationQuery = classificationService.createClassificationQuery();
            if (params.containsKey(CLASSIFICATION_PARENT_KEY)) {
                String[] parentClassifications = extractCommaSeperatedFields(params.get(CLASSIFICATION_PARENT_KEY));
                classificationQuery.parentClassificationKey(parentClassifications);
            }
            if (params.containsKey(CLASSIFICATION_CATEGORY)) {
                String[] categories = extractCommaSeperatedFields(params.get(CLASSIFICATION_CATEGORY));
                classificationQuery.category(categories);
            }
            if (params.containsKey(CLASSIFICATION_TYPE)) {
                String[] types = extractCommaSeperatedFields(params.get(CLASSIFICATION_TYPE));
                classificationQuery.type(types);
            }
            if (params.containsKey(CLASSIFICATION_NAME)) {
                String[] names = extractCommaSeperatedFields(params.get(CLASSIFICATION_NAME));
                classificationQuery.name(names);
            }
            if (params.containsKey(CLASSIFICATION_DESCRIPTION)) {
                classificationQuery.descriptionLike(params.get(CLASSIFICATION_DESCRIPTION).get(0));
            }
            if (params.containsKey(CLASSIFICATION_PRIORITY)) {
                String[] prioritesInString = extractCommaSeperatedFields(params.get(CLASSIFICATION_PRIORITY));
                int[] priorites = extractPriorities(prioritesInString);
                classificationQuery.priority(priorites);
            }
            if (params.containsKey(CLASSIFICATION_SERVICE_LEVEL)) {
                String[] serviceLevels = extractCommaSeperatedFields(params.get(CLASSIFICATION_SERVICE_LEVEL));
                classificationQuery.serviceLevel(serviceLevels);
            }
            taskQuery.classification(classificationQuery);
        }
        if (params.containsKey(WORKBASKET_ID)) {
            String[] workbaskets = extractCommaSeperatedFields(params.get(WORKBASKET_ID));
            taskQuery.workbasketId(workbaskets);
        }
        if (params.containsKey(OWNER)) {
            String[] owners = extractCommaSeperatedFields(params.get(OWNER));
            taskQuery.owner(owners);
        }
        // objectReference
        if (params.keySet().stream().filter(s -> s.startsWith(POR)).toArray().length > 0) {
            ObjectReferenceQuery objectReferenceQuery = taskQuery.createObjectReferenceQuery();
            if (params.containsKey(POR_COMPANY)) {
                String[] companies = extractCommaSeperatedFields(params.get(POR_COMPANY));
                objectReferenceQuery.company(companies);
            }
            if (params.containsKey(POR_SYSTEM)) {
                String[] systems = extractCommaSeperatedFields(params.get(POR_SYSTEM));
                objectReferenceQuery.system(systems);
            }
            if (params.containsKey(POR_SYSTEM_INSTANCE)) {
                String[] systemInstances = extractCommaSeperatedFields(params.get(POR_SYSTEM_INSTANCE));
                objectReferenceQuery.systemInstance(systemInstances);
            }
            if (params.containsKey(POR_TYPE)) {
                String[] types = extractCommaSeperatedFields(params.get(POR_TYPE));
                objectReferenceQuery.type(types);
            }
            if (params.containsKey(POR_VALUE)) {
                String[] values = extractCommaSeperatedFields(params.get(POR_VALUE));
                objectReferenceQuery.value(values);
            }

            taskQuery.objectReference(objectReferenceQuery);
        }
        if (params.containsKey(IS_READ)) {
            taskQuery.read(Boolean.getBoolean(params.get(IS_READ).get(0)));
        }
        if (params.containsKey(IS_TRANSFERRED)) {
            taskQuery.transferred(Boolean.getBoolean(params.get(IS_TRANSFERRED).get(0)));
        }
        if (params.containsKey(CUSTOM)) {
            String[] custom = extractCommaSeperatedFields(params.get(CUSTOM));
            taskQuery.customFields(custom);
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
        list.stream().forEach(item -> {
            Arrays.asList(item.split(COMMA)).stream().forEach(subItem -> {
                values.add(subItem);
            });
        });
        return values.toArray(new String[0]);
    }

    private TaskState[] extractStates(MultiValueMap<String, String> params) {
        List<TaskState> states = new ArrayList<>();
        params.get(STATE).stream().forEach(item -> {
            Arrays.asList(item.split(COMMA)).stream().forEach(state -> {
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
                }
            });
        });
        return states.toArray(new TaskState[0]);
    }
}
