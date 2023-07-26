package pro.taskana.workbasket.internal;

import static pro.taskana.common.api.BaseQuery.toLowerCopy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;
import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.TaskanaRuntimeException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.workbasket.api.WorkbasketCustomField;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketQuery;
import pro.taskana.workbasket.api.WorkbasketQueryColumnName;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** WorkbasketQuery for generating dynamic SQL. */
public class WorkbasketQueryImpl implements WorkbasketQuery {

  private static final String LINK_TO_MAPPER =
      "pro.taskana.workbasket.internal.WorkbasketQueryMapper.queryWorkbasketSummaries";
  private static final String LINK_TO_COUNTER =
      "pro.taskana.workbasket.internal.WorkbasketQueryMapper.countQueryWorkbaskets";
  private static final String LINK_TO_VALUEMAPPER =
      "pro.taskana.workbasket.internal.WorkbasketQueryMapper.queryWorkbasketColumnValues";
  private final InternalTaskanaEngine taskanaEngine;
  private final List<String> orderBy;
  private final List<String> orderColumns;
  private WorkbasketQueryColumnName columnName;
  private String[] accessIds;
  private String[] idIn;
  private WorkbasketPermission[] permissions;
  private String[] nameIn;
  private String[] nameLike;
  private String[] keyIn;
  private String[] keyLike;
  private String[] keyOrNameLike;
  private String[] domainIn;
  private String[] domainLike;
  private WorkbasketType[] type;
  private TimeInterval[] createdIn;
  private TimeInterval[] modifiedIn;
  private String[] descriptionLike;
  private String[] ownerIn;
  private String[] ownerLike;
  private String[] custom1In;
  private String[] custom1Like;
  private String[] custom2In;
  private String[] custom2Like;
  private String[] custom3In;
  private String[] custom3Like;
  private String[] custom4In;
  private String[] custom4Like;
  private String[] orgLevel1In;
  private String[] orgLevel1Like;
  private String[] orgLevel2In;
  private String[] orgLevel2Like;
  private String[] orgLevel3In;
  private String[] orgLevel3Like;
  private String[] orgLevel4In;
  private String[] orgLevel4Like;
  private Boolean markedForDeletion;
  private boolean joinWithAccessList;
  private boolean checkReadPermission;
  private boolean usedToAugmentTasks;
  private boolean callerRolesAndAccessIdsAlreadyHandled;

  WorkbasketQueryImpl(InternalTaskanaEngine taskanaEngine) {
    this.taskanaEngine = taskanaEngine;
    this.orderBy = new ArrayList<>();
    this.orderColumns = new ArrayList<>();
    this.callerRolesAndAccessIdsAlreadyHandled = false;
  }

  @Override
  public WorkbasketQuery idIn(String... ids) {
    if (ids != null && ids.length != 0) {
      this.idIn = ids;
    }
    return this;
  }

  @Override
  public WorkbasketQuery keyIn(String... keys) {
    if (keys != null && keys.length != 0) {
      this.keyIn = keys;
    }
    return this;
  }

  @Override
  public WorkbasketQuery keyLike(String... keys) {
    this.keyLike = toLowerCopy(keys);
    return this;
  }

  @Override
  public WorkbasketQuery nameIn(String... names) {
    this.nameIn = names;
    return this;
  }

  @Override
  public WorkbasketQuery nameLike(String... names) {
    this.nameLike = toLowerCopy(names);
    return this;
  }

  @Override
  public WorkbasketQuery keyOrNameLike(String... keysOrNames) {
    this.keyOrNameLike = toLowerCopy(keysOrNames);
    return this;
  }

  @Override
  public WorkbasketQuery domainIn(String... domains) {
    this.domainIn = domains;
    return this;
  }

  @Override
  public WorkbasketQuery typeIn(WorkbasketType... types) {
    this.type = types;
    return this;
  }

  @Override
  public WorkbasketQuery createdWithin(TimeInterval... intervals) {
    validateAllTimeIntervals(intervals);
    this.createdIn = intervals;
    return this;
  }

  @Override
  public WorkbasketQuery modifiedWithin(TimeInterval... intervals) {
    validateAllTimeIntervals(intervals);
    this.modifiedIn = intervals;
    return this;
  }

  @Override
  public WorkbasketQuery descriptionLike(String... descriptions) {
    this.descriptionLike = toLowerCopy(descriptions);
    return this;
  }

  @Override
  public WorkbasketQuery ownerIn(String... owners) {
    this.ownerIn = owners;
    return this;
  }

  @Override
  public WorkbasketQuery ownerLike(String... owners) {
    this.ownerLike = toLowerCopy(owners);
    return this;
  }

  @Override
  public WorkbasketQuery accessIdsHavePermissions(
      List<WorkbasketPermission> permissions, String... accessIds)
      throws InvalidArgumentException, NotAuthorizedException {
    taskanaEngine
        .getEngine()
        .checkRoleMembership(TaskanaRole.ADMIN, TaskanaRole.BUSINESS_ADMIN, TaskanaRole.TASK_ADMIN);
    // Checking pre-conditions
    if (permissions == null || permissions.isEmpty()) {
      throw new InvalidArgumentException("Permissions can't be null or empty.");
    }
    if (accessIds == null || accessIds.length == 0) {
      throw new InvalidArgumentException("accessIds can't be NULL or empty.");
    }

    // set up permissions and ids
    this.permissions = permissions.toArray(WorkbasketPermission[]::new);
    this.accessIds = accessIds;
    lowercaseAccessIds(this.accessIds);

    return this;
  }

  @Override
  public WorkbasketQuery callerHasPermissions(WorkbasketPermission... permissions) {
    this.permissions = permissions;
    return this;
  }

  @Override
  public WorkbasketQuery orderByName(SortDirection sortDirection) {
    return addOrderCriteria("NAME", sortDirection);
  }

  @Override
  public WorkbasketQuery orderByKey(SortDirection sortDirection) {
    return addOrderCriteria("KEY", sortDirection);
  }

  @Override
  public WorkbasketQuery orderByDescription(SortDirection sortDirection) {
    return addOrderCriteria("DESCRIPTION", sortDirection);
  }

  @Override
  public WorkbasketQuery orderByOwner(SortDirection sortDirection) {
    return addOrderCriteria("OWNER", sortDirection);
  }

  @Override
  public WorkbasketQuery orderByType(SortDirection sortDirection) {
    return addOrderCriteria("TYPE", sortDirection);
  }

  @Override
  public WorkbasketQuery orderByDomain(SortDirection sortDirection) {
    return addOrderCriteria("DOMAIN", sortDirection);
  }

  @Override
  public WorkbasketQuery domainLike(String... domains) {
    this.domainLike = toLowerCopy(domains);
    return this;
  }

  @Override
  public WorkbasketQuery orderByCustomAttribute(
      WorkbasketCustomField customField, SortDirection sortDirection) {
    return addOrderCriteria(customField.name(), sortDirection);
  }

  @Override
  public WorkbasketQuery orderByOrgLevel1(SortDirection sortDirection) {
    return addOrderCriteria("ORG_LEVEL_1", sortDirection);
  }

  @Override
  public WorkbasketQuery orderByOrgLevel2(SortDirection sortDirection) {
    return addOrderCriteria("ORG_LEVEL_2", sortDirection);
  }

  @Override
  public WorkbasketQuery orderByOrgLevel3(SortDirection sortDirection) {
    return addOrderCriteria("ORG_LEVEL_3", sortDirection);
  }

  @Override
  public WorkbasketQuery orderByOrgLevel4(SortDirection sortDirection) {
    return addOrderCriteria("ORG_LEVEL_4", sortDirection);
  }

  @Override
  public WorkbasketQuery customAttributeIn(
      WorkbasketCustomField customField, String... searchArguments) {
    switch (customField) {
      case CUSTOM_1:
        custom1In = searchArguments;
        break;
      case CUSTOM_2:
        custom2In = searchArguments;
        break;
      case CUSTOM_3:
        custom3In = searchArguments;
        break;
      case CUSTOM_4:
        custom4In = searchArguments;
        break;
      default:
        throw new SystemException("Unknown customField '" + customField + "'");
    }
    return this;
  }

  @Override
  public WorkbasketQuery customAttributeLike(
      WorkbasketCustomField customField, String... searchArguments) {
    switch (customField) {
      case CUSTOM_1:
        custom1Like = toLowerCopy(searchArguments);
        break;
      case CUSTOM_2:
        custom2Like = toLowerCopy(searchArguments);
        break;
      case CUSTOM_3:
        custom3Like = toLowerCopy(searchArguments);
        break;
      case CUSTOM_4:
        custom4Like = toLowerCopy(searchArguments);
        break;
      default:
        throw new SystemException("Unknown customField '" + customField + "'");
    }
    return this;
  }

  @Override
  public WorkbasketQuery orgLevel1In(String... orgLevels1) {
    this.orgLevel1In = orgLevels1;
    return this;
  }

  @Override
  public WorkbasketQuery orgLevel1Like(String... orgLevels1) {
    this.orgLevel1Like = toLowerCopy(orgLevels1);
    return this;
  }

  @Override
  public WorkbasketQuery orgLevel2In(String... orgLevels2) {
    this.orgLevel2In = orgLevels2;
    return this;
  }

  @Override
  public WorkbasketQuery orgLevel2Like(String... orgLevels2) {
    this.orgLevel2Like = toLowerCopy(orgLevels2);
    return this;
  }

  @Override
  public WorkbasketQuery orgLevel3In(String... orgLevels3) {
    this.orgLevel3In = orgLevels3;
    return this;
  }

  @Override
  public WorkbasketQuery orgLevel3Like(String... orgLevels3) {
    this.orgLevel3Like = toLowerCopy(orgLevels3);
    return this;
  }

  @Override
  public WorkbasketQuery orgLevel4In(String... orgLevels4) {
    this.orgLevel4In = orgLevels4;
    return this;
  }

  @Override
  public WorkbasketQuery orgLevel4Like(String... orgLevels4) {
    this.orgLevel4Like = toLowerCopy(orgLevels4);
    return this;
  }

  @Override
  public WorkbasketQuery markedForDeletion(boolean markedForDeletion) {
    this.markedForDeletion = markedForDeletion;
    return this;
  }

  @Override
  public List<WorkbasketSummary> list() {
    handleCallerRolesAndAccessIds();
    return taskanaEngine.executeInDatabaseConnection(
        () -> taskanaEngine.getSqlSession().selectList(LINK_TO_MAPPER, this));
  }

  @Override
  public List<WorkbasketSummary> list(int offset, int limit) {
    List<WorkbasketSummary> workbaskets = new ArrayList<>();
    try {
      taskanaEngine.openConnection();
      RowBounds rowBounds = new RowBounds(offset, limit);
      handleCallerRolesAndAccessIds();
      workbaskets = taskanaEngine.getSqlSession().selectList(LINK_TO_MAPPER, this, rowBounds);
      return workbaskets;
    } catch (PersistenceException e) {
      if (e.getMessage().contains("ERRORCODE=-4470")) {
        TaskanaRuntimeException ex =
            new SystemException(
                "The offset beginning was set over the amount of result-rows.", e.getCause());
        ex.setStackTrace(e.getStackTrace());
        throw ex;
      }
      throw e;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  public List<String> listValues(
      WorkbasketQueryColumnName columnName, SortDirection sortDirection) {
    List<String> result = new ArrayList<>();
    try {
      taskanaEngine.openConnection();
      this.columnName = columnName;
      handleCallerRolesAndAccessIds();
      this.orderBy.clear();
      result = taskanaEngine.getSqlSession().selectList(LINK_TO_VALUEMAPPER, this);
      return result;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public WorkbasketSummary single() {
    WorkbasketSummary workbasket = null;
    try {
      taskanaEngine.openConnection();
      handleCallerRolesAndAccessIds();
      workbasket = taskanaEngine.getSqlSession().selectOne(LINK_TO_MAPPER, this);
      return workbasket;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public long count() {
    Long rowCount = null;
    try {
      taskanaEngine.openConnection();
      handleCallerRolesAndAccessIds();
      rowCount = taskanaEngine.getSqlSession().selectOne(LINK_TO_COUNTER, this);
      return (rowCount == null) ? 0L : rowCount;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  public String[] getAccessIds() {
    return accessIds;
  }

  public WorkbasketPermission[] getPermissions() {
    return permissions;
  }

  public String[] getNameIn() {
    return nameIn;
  }

  public String[] getNameLike() {
    return nameLike;
  }

  public String[] getKeyIn() {
    return keyIn;
  }

  public String[] getKeyLike() {
    return keyLike;
  }

  public String[] getKeyOrNameLike() {
    return keyOrNameLike;
  }

  public WorkbasketType[] getType() {
    return type;
  }

  public TimeInterval[] getCreatedIn() {
    return createdIn;
  }

  public TimeInterval[] getModifiedIn() {
    return modifiedIn;
  }

  public String[] getDescriptionLike() {
    return descriptionLike;
  }

  public String[] getOwnerIn() {
    return ownerIn;
  }

  public String[] getDomainIn() {
    return domainIn;
  }

  public String[] getDomainLike() {
    return domainLike;
  }

  public String[] getCustom1In() {
    return custom1In;
  }

  public String[] getCustom1Like() {
    return custom1Like;
  }

  public String[] getCustom2In() {
    return custom2In;
  }

  public String[] getCustom2Like() {
    return custom2Like;
  }

  public String[] getCustom3In() {
    return custom3In;
  }

  public String[] getCustom3Like() {
    return custom3Like;
  }

  public String[] getCustom4In() {
    return custom4In;
  }

  public String[] getCustom4Like() {
    return custom4Like;
  }

  public String[] getOrgLevel1In() {
    return orgLevel1In;
  }

  public String[] getOrgLevel1Like() {
    return orgLevel1Like;
  }

  public String[] getOrgLevel2In() {
    return orgLevel2In;
  }

  public String[] getOrgLevel2Like() {
    return orgLevel2Like;
  }

  public String[] getOrgLevel3In() {
    return orgLevel3In;
  }

  public String[] getOrgLevel3Like() {
    return orgLevel3Like;
  }

  public String[] getOrgLevel4In() {
    return orgLevel4In;
  }

  public String[] getOrgLevel4Like() {
    return orgLevel4Like;
  }

  public Boolean isMarkedForDeletion() {
    return markedForDeletion;
  }

  public String[] getOwnerLike() {
    return ownerLike;
  }

  public String[] getIdIn() {
    return idIn;
  }

  public List<String> getOrderBy() {
    return orderBy;
  }

  public List<String> getOrderColumns() {
    return orderColumns;
  }

  public WorkbasketQueryColumnName getColumnName() {
    return columnName;
  }

  public boolean isJoinWithAccessList() {
    return joinWithAccessList;
  }

  public boolean isCheckReadPermission() {
    return checkReadPermission;
  }

  public void setUsedToAugmentTasks(boolean usedToAugmentTasks) {
    this.usedToAugmentTasks = usedToAugmentTasks;
  }

  public static void lowercaseAccessIds(String[] accessIdArray) {
    if (TaskanaConfiguration.shouldUseLowerCaseForAccessIds()) {
      for (int i = 0; i < accessIdArray.length; i++) {
        String id = accessIdArray[i];
        if (id != null) {
          accessIdArray[i] = id.toLowerCase();
        }
      }
    }
  }

  private void validateAllTimeIntervals(TimeInterval[] intervals) {
    for (TimeInterval ti : intervals) {
      if (!ti.isValid()) {
        throw new IllegalArgumentException("TimeInterval " + ti + " is invalid.");
      }
    }
  }

  private void handleCallerRolesAndAccessIds() {
    if (!callerRolesAndAccessIdsAlreadyHandled) {
      callerRolesAndAccessIdsAlreadyHandled = true;

      // if user is admin, taskadmin or businessadmin, don't check read permission on workbasket.
      // in addition, if user is admin, taskadmin or businessadmin and no accessIds were specified,
      // don't join
      // with access
      // list
      // if this query is used to augment task or a permission is given as filter criteria,
      // a business admin should be treated like a normal user
      //
      // (joinWithAccessList,checkReadPermission) can assume the following combinations:
      // (t,t) -> query performed by user
      // (f,f) -> admin/task admin queries w/o access ids specified
      // (t,f) -> business admin queries with access ids specified or permissions given
      // (f,t) -> cannot happen, cannot be matched to meaningful query
      joinWithAccessList = true;
      checkReadPermission = true;
      if (taskanaEngine.getEngine().isUserInRole(TaskanaRole.ADMIN, TaskanaRole.TASK_ADMIN)
          && accessIds == null) {
        checkReadPermission = false;
        joinWithAccessList = false;
      } else if (taskanaEngine.getEngine().isUserInRole(TaskanaRole.BUSINESS_ADMIN)
          && !usedToAugmentTasks) {
        checkReadPermission = false;
        if (accessIds == null && (permissions == null || permissions.length == 0)) {
          joinWithAccessList = false;
        }
      }
      // might already be set by accessIdsHavePermissions
      if (this.accessIds == null) {
        String[] accessIds = new String[0];
        List<String> ucAccessIds = taskanaEngine.getEngine().getCurrentUserContext().getAccessIds();
        if (!ucAccessIds.isEmpty()) {
          accessIds = ucAccessIds.toArray(accessIds);
        }
        this.accessIds = accessIds;
        lowercaseAccessIds(this.accessIds);
      }
    }
  }

  private WorkbasketQuery addOrderCriteria(String colName, SortDirection sortDirection) {
    String orderByDirection =
        " " + (sortDirection == null ? SortDirection.ASCENDING : sortDirection);
    orderBy.add(colName + orderByDirection);
    orderColumns.add(colName);
    return this;
  }

  @Override
  public String toString() {
    return "WorkbasketQueryImpl [columnName="
        + columnName
        + ", accessId="
        + Arrays.toString(accessIds)
        + ", idIn="
        + Arrays.toString(idIn)
        + ", permission="
        + Arrays.toString(permissions)
        + ", nameIn="
        + Arrays.toString(nameIn)
        + ", nameLike="
        + Arrays.toString(nameLike)
        + ", keyIn="
        + Arrays.toString(keyIn)
        + ", keyLike="
        + Arrays.toString(keyLike)
        + ", keyOrNameLike="
        + Arrays.toString(keyOrNameLike)
        + ", domainIn="
        + Arrays.toString(domainIn)
        + ", domainLike="
        + Arrays.toString(domainLike)
        + ", type="
        + Arrays.toString(type)
        + ", createdIn="
        + Arrays.toString(createdIn)
        + ", modifiedIn="
        + Arrays.toString(modifiedIn)
        + ", descriptionLike="
        + Arrays.toString(descriptionLike)
        + ", ownerIn="
        + Arrays.toString(ownerIn)
        + ", ownerLike="
        + Arrays.toString(ownerLike)
        + ", custom1In="
        + Arrays.toString(custom1In)
        + ", custom1Like="
        + Arrays.toString(custom1Like)
        + ", custom2In="
        + Arrays.toString(custom2In)
        + ", custom2Like="
        + Arrays.toString(custom2Like)
        + ", custom3In="
        + Arrays.toString(custom3In)
        + ", custom3Like="
        + Arrays.toString(custom3Like)
        + ", custom4In="
        + Arrays.toString(custom4In)
        + ", custom4Like="
        + Arrays.toString(custom4Like)
        + ", orgLevel1In="
        + Arrays.toString(orgLevel1In)
        + ", orgLevel1Like="
        + Arrays.toString(orgLevel1Like)
        + ", orgLevel2In="
        + Arrays.toString(orgLevel2In)
        + ", orgLevel2Like="
        + Arrays.toString(orgLevel2Like)
        + ", orgLevel3In="
        + Arrays.toString(orgLevel3In)
        + ", orgLevel3Like="
        + Arrays.toString(orgLevel3Like)
        + ", orgLevel4In="
        + Arrays.toString(orgLevel4In)
        + ", orgLevel4Like="
        + Arrays.toString(orgLevel4Like)
        + ", markedForDeletion="
        + markedForDeletion
        + ", taskanaEngine="
        + taskanaEngine
        + ", orderBy="
        + orderBy
        + ", orderColumns="
        + orderColumns
        + ", joinWithAccessList="
        + joinWithAccessList
        + ", checkReadPermission="
        + checkReadPermission
        + ", usedToAugmentTasks="
        + usedToAugmentTasks
        + ", callerRolesAndAccessIdsAlreadyHandled="
        + callerRolesAndAccessIdsAlreadyHandled
        + "]";
  }
}
