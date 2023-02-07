package pro.taskana.common.internal.persistence;

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

/** Instruct jdbc driver to interpret timestamps as being in utc timezone. */
public class InstantTypeHandler extends BaseTypeHandler<Instant> {

  static final TimeZone TIME_ZONE_UTC = TimeZone.getTimeZone("UTC");

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, Instant parameter, JdbcType jdbcType)
      throws SQLException {
    ps.setTimestamp(i, Timestamp.from(parameter), Calendar.getInstance(TIME_ZONE_UTC));
  }

  @Override
  public Instant getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return getInstant(rs.getTimestamp(columnName, Calendar.getInstance(TIME_ZONE_UTC)));
  }

  @Override
  public Instant getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return getInstant(rs.getTimestamp(columnIndex, Calendar.getInstance(TIME_ZONE_UTC)));
  }

  @Override
  public Instant getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return getInstant(cs.getTimestamp(columnIndex, Calendar.getInstance(TIME_ZONE_UTC)));
  }

  private static Instant getInstant(Timestamp timestamp) {
    if (timestamp != null) {
      return timestamp.toInstant();
    }
    return null;
  }
}
