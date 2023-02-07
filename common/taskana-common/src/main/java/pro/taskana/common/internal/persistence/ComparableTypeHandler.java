package pro.taskana.common.internal.persistence;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * TypeHandler for ComparableTypes when using Interval. When using new Interval Types you need to
 * extend this class
 */
public class ComparableTypeHandler extends BaseTypeHandler<Comparable> {

  private static final String UNSUPPORTED_TYPE_OF_COMPARABLE = "Unsupported Type of Comparable";

  @Override
  public void setNonNullParameter(
      PreparedStatement ps, int i, Comparable parameter, JdbcType jdbcType) throws SQLException {
    if (parameter instanceof Integer) {
      ps.setInt(i, (Integer) parameter);
    } else if (parameter instanceof Instant) {
      ps.setTimestamp(
          i,
          Timestamp.from((Instant) parameter),
          Calendar.getInstance(InstantTypeHandler.TIME_ZONE_UTC));
    } else {
      throw new SQLException(UNSUPPORTED_TYPE_OF_COMPARABLE);
    }
  }

  @Override
  public Comparable getNullableResult(ResultSet rs, String columnName) throws SQLException {
    int count = rs.getMetaData().getColumnCount();
    for (int i = 1; i <= count; i++) {
      if (rs.getMetaData().getColumnName(i).equals(columnName)) {
        return getNullableResult(rs, i);
      }
    }
    throw new SQLException("Could not determine ColumnIndex");
  }

  @Override
  public Comparable getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    String columnTypeName = rs.getMetaData().getColumnClassName(columnIndex);
    Comparable result;
    if (Integer.class.getSimpleName().equals(columnTypeName)) {
      result = rs.getInt(columnIndex);
    } else if (Timestamp.class.getSimpleName().equals(columnTypeName)) {
      result =
          getInstant(
              rs.getTimestamp(columnIndex, Calendar.getInstance(InstantTypeHandler.TIME_ZONE_UTC)));
    } else {
      throw new SQLException(UNSUPPORTED_TYPE_OF_COMPARABLE);
    }

    return result;
  }

  @Override
  public Comparable getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    String columnTypeName = cs.getMetaData().getColumnClassName(columnIndex);
    Comparable result;
    if (Integer.class.getSimpleName().equals(columnTypeName)) {
      result = cs.getInt(columnIndex);
    } else if (Timestamp.class.getSimpleName().equals(columnTypeName)) {
      result =
          getInstant(
              cs.getTimestamp(columnIndex, Calendar.getInstance(InstantTypeHandler.TIME_ZONE_UTC)));
    } else {
      throw new SQLException(UNSUPPORTED_TYPE_OF_COMPARABLE);
    }

    return result;
  }

  private static Instant getInstant(Timestamp timestamp) {
    if (timestamp != null) {
      return timestamp.toInstant();
    }
    return null;
  }
}
