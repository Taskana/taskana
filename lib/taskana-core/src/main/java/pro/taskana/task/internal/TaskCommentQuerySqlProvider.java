package pro.taskana.task.internal;

import static pro.taskana.common.internal.util.SqlProviderUtil.CLOSING_SCRIPT_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.CLOSING_WHERE_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.DB2_WITH_UR;
import static pro.taskana.common.internal.util.SqlProviderUtil.OPENING_SCRIPT_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.OPENING_WHERE_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereIn;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereInTime;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereLike;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereNotIn;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereNotInTime;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereNotLike;

import java.util.Arrays;
import java.util.stream.Collectors;

import pro.taskana.task.api.TaskCommentQueryColumnName;

public class TaskCommentQuerySqlProvider {

  private TaskCommentQuerySqlProvider() {}

  @SuppressWarnings("unused")
  public static String queryTaskComments() {
    return OPENING_SCRIPT_TAG
        + "SELECT "
        + commonSelectFields()
        + "<if test=\"joinWithUserInfo\">"
        + ", u.FULL_NAME"
        + "</if>"
        + "FROM TASK_COMMENT tc "
        + "LEFT JOIN Task AS t ON tc.TASK_ID = t.ID "
        + "<if test=\"joinWithUserInfo\">"
        + "LEFT JOIN USER_INFO AS u ON tc.CREATOR = u.USER_ID "
        + "</if>"
        + OPENING_WHERE_TAG
        + checkForAuthorization()
        + commonTaskCommentWhereStatement()
        + CLOSING_WHERE_TAG
        + "<if test='!orderBy.isEmpty()'>"
        + "ORDER BY <foreach item='item' collection='orderBy' separator=',' >${item}</foreach>"
        + "</if> "
        + CLOSING_SCRIPT_TAG;
  }

  @SuppressWarnings("unused")
  public static String countQueryTaskComments() {
    return OPENING_SCRIPT_TAG
        + "SELECT COUNT(tc.ID) "
        + "FROM TASK_COMMENT tc "
        + "LEFT JOIN Task AS t ON tc.TASK_ID = t.ID "
        + OPENING_WHERE_TAG
        + checkForAuthorization()
        + commonTaskCommentWhereStatement()
        + CLOSING_WHERE_TAG
        + CLOSING_SCRIPT_TAG;
  }

  @SuppressWarnings("unused")
  public static String queryTaskCommentColumnValues() {
    return OPENING_SCRIPT_TAG
        + "SELECT DISTINCT ${queryColumnName} "
        + "FROM TASK_COMMENT tc "
        + "LEFT JOIN Task AS t ON tc.TASK_ID = t.ID "
        + "<if test=\"joinWithUserInfo\">"
        + "LEFT JOIN USER_INFO AS u ON tc.CREATOR = u.USER_ID "
        + "</if>"
        + OPENING_WHERE_TAG
        + checkForAuthorization()
        + commonTaskCommentWhereStatement()
        + CLOSING_WHERE_TAG
        + DB2_WITH_UR
        + CLOSING_SCRIPT_TAG;
  }

  private static String commonSelectFields() {
    // includes only the names that start with tc, because other columns are conditional
    return Arrays.stream(TaskCommentQueryColumnName.values())
        .map(TaskCommentQueryColumnName::toString)
        .filter(column -> column.startsWith("tc"))
        .collect(Collectors.joining(", "));
  }

  private static String commonTaskCommentWhereStatement() {
    StringBuilder sb = new StringBuilder();
    whereIn("idIn", "tc.ID", sb);
    whereNotIn("idNotIn", "tc.ID", sb);
    whereLike("idLike", "tc.ID", sb);
    whereNotLike("idNotLike", "tc.ID", sb);
    whereIn("taskIdIn", "tc.TASK_ID", sb);
    whereLike("textFieldLike", "tc.TEXT_FIELD", sb);
    whereNotLike("textFieldNotLike", "tc.TEXT_FIELD", sb);
    whereIn("creatorIn", "tc.CREATOR", sb);
    whereNotIn("creatorNotIn", "tc.CREATOR", sb);
    whereLike("creatorLike", "tc.CREATOR", sb);
    whereNotLike("creatorNotLike", "tc.CREATOR", sb);
    whereInTime("createdIn", "tc.CREATED", sb);
    whereNotInTime("createdNotIn", "tc.CREATED", sb);
    whereInTime("modifiedIn", "tc.MODIFIED", sb);
    whereNotInTime("modifiedNotIn", "tc.MODIFIED", sb);
    return sb.toString();
  }

  private static String checkForAuthorization() {
    return "<if test='accessIdIn != null'> AND t.WORKBASKET_ID IN ("
        + "SELECT WID "
        + "FROM ("
        + "SELECT WORKBASKET_ID as WID, MAX(PERM_READ::int) as MAX_READ "
        + "FROM WORKBASKET_ACCESS_LIST AS s where ACCESS_ID IN "
        + "(<foreach item='item' collection='accessIdIn' separator=',' >#{item}</foreach>) "
        + "GROUP by WORKBASKET_ID) as f "
        + "WHERE MAX_READ = 1) "
        + "</if>";
  }
}
