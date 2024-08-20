package io.kadai.common.internal.persistence;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This Typehandler will transfer a Map into an xml blob and back. */
public class MapTypeHandler extends BaseTypeHandler<Map<String, Object>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(MapTypeHandler.class);

  @Override
  public void setNonNullParameter(
      PreparedStatement ps, int i, Map<String, Object> parameter, JdbcType jdbcType)
      throws SQLException {
    if (parameter != null && parameter.size() > 0) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Input-Map before serializing: {}", parameter);
      }
      // Convert Map to JSON string
      JSONObject jsonObj = new JSONObject(parameter);
      ps.setString(i, jsonObj.toString());
    } else {
      ps.setNull(i, Types.BLOB);
    }
  }

  @Override
  public Map<String, Object> getNullableResult(ResultSet rs, String columnName)
      throws SQLException {
    String fieldValue = rs.getString(columnName);
    if (fieldValue != null) {
      return convertToMap(fieldValue);
    }
    return null;
  }

  @Override
  public Map<String, Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    String fieldValue = rs.getString(columnIndex);
    if (fieldValue != null) {
      return convertToMap(fieldValue);
    }
    return null;
  }

  @Override
  public Map<String, Object> getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    String fieldValue = cs.getString(columnIndex);
    if (fieldValue != null) {
      return convertToMap(fieldValue);
    }
    return null;
  }

  private Map<String, Object> convertToMap(String fieldValue) {
    JSONObject jsonObj = new JSONObject(fieldValue);
    return jsonObj.toMap();
  }
}
