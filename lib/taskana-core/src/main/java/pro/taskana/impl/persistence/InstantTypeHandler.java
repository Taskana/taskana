package pro.taskana.impl.persistence;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import java.util.TimeZone;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * Instruct jdbc driver to interpret timestamps as being in utc timezone.
 * @author bbr
 *
 */

public class InstantTypeHandler  extends BaseTypeHandler<Instant> {
  private static TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
  private static Calendar calendar = Calendar.getInstance(utcTimeZone);

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, Instant parameter,
      JdbcType jdbcType) throws SQLException {
    ps.setTimestamp(i, Timestamp.from(parameter), calendar);
  }

  @Override
  public Instant getNullableResult(ResultSet rs, String columnName) throws SQLException {
    Timestamp timestamp = rs.getTimestamp(columnName, calendar);
    return getInstant(timestamp);
  }

  @Override
  public Instant getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    Timestamp timestamp = rs.getTimestamp(columnIndex, calendar);
    return getInstant(timestamp);
  }

  @Override
  public Instant getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    Timestamp timestamp = cs.getTimestamp(columnIndex, calendar);
    return getInstant(timestamp);
  }

  private static Instant getInstant(Timestamp timestamp) {
    if (timestamp != null) {
      return timestamp.toInstant();
    }
    return null;
  }

}
