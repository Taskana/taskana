package io.kadai.task.internal;

import static io.kadai.common.internal.util.SqlProviderUtil.CLOSING_SCRIPT_TAG;
import static io.kadai.common.internal.util.SqlProviderUtil.CLOSING_WHERE_TAG;
import static io.kadai.common.internal.util.SqlProviderUtil.DB2_WITH_UR;
import static io.kadai.common.internal.util.SqlProviderUtil.OPENING_SCRIPT_TAG;
import static io.kadai.common.internal.util.SqlProviderUtil.OPENING_WHERE_TAG;
import static io.kadai.common.internal.util.SqlProviderUtil.whereIn;
import static io.kadai.common.internal.util.SqlProviderUtil.whereInInterval;
import static io.kadai.common.internal.util.SqlProviderUtil.whereLike;
import static io.kadai.common.internal.util.SqlProviderUtil.whereNotIn;
import static io.kadai.common.internal.util.SqlProviderUtil.whereNotInInterval;
import static io.kadai.common.internal.util.SqlProviderUtil.whereNotLike;

import io.kadai.task.api.TaskCommentQueryColumnName;
import java.util.Arrays;
import java.util.stream.Collectors;

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
        + "LEFT JOIN Task t ON tc.TASK_ID = t.ID "
        + "<if test=\"joinWithUserInfo\">"
        + "LEFT JOIN USER_INFO u ON tc.CREATOR = u.USER_ID "
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
        + "LEFT JOIN Task t ON tc.TASK_ID = t.ID "
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
        + "LEFT JOIN Task t ON tc.TASK_ID = t.ID "
        + "<if test=\"joinWithUserInfo\">"
        + "LEFT JOIN USER_INFO u ON tc.CREATOR = u.USER_ID "
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
    whereInInterval("createdIn", "tc.CREATED", sb);
    whereNotInInterval("createdNotIn", "tc.CREATED", sb);
    whereInInterval("modifiedIn", "tc.MODIFIED", sb);
    whereNotInInterval("modifiedNotIn", "tc.MODIFIED", sb);
    return sb.toString();
  }

  private static String checkForAuthorization() {
    return "<if test='accessIdIn != null'> AND t.WORKBASKET_ID IN ("
        + "SELECT WID "
        + "FROM ("
        + "<choose>"
        + "<when test=\"_databaseId == 'db2' || _databaseId == 'oracle'\">"
        + "SELECT WORKBASKET_ID as WID, MAX(PERM_READ) as MAX_READ "
        + "</when>"
        + "<otherwise>"
        + "SELECT WORKBASKET_ID as WID, MAX(PERM_READ::int) as MAX_READ "
        + "</otherwise>"
        + "</choose>"
        + "FROM WORKBASKET_ACCESS_LIST s "
        + "WHERE ACCESS_ID IN "
        + "(<foreach item='item' collection='accessIdIn' separator=',' >#{item}</foreach>) "
        + "GROUP by WORKBASKET_ID) f "
        + "WHERE MAX_READ = 1) "
        + "</if>";
  }
}
