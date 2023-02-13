package pro.taskana.common.internal.persistence;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class StringTypeHandler extends BaseTypeHandler<String> {

  public static final String EMPTY_PLACEHOLDER = "#EMPTY#";

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
      throws SQLException {
    ps.setString(i, "".equals(parameter) ? EMPTY_PLACEHOLDER : parameter);
  }

  @Override
  public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
    String dbString = rs.getString(columnName);
    return EMPTY_PLACEHOLDER.equals(dbString) ? "" : dbString;
  }

  @Override
  public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    String dbString = rs.getString(columnIndex);
    return EMPTY_PLACEHOLDER.equals(dbString) ? "" : dbString;
  }

  @Override
  public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    String dbString = cs.getString(columnIndex);
    return EMPTY_PLACEHOLDER.equals(dbString) ? "" : dbString;
  }
}
