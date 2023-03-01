/*-
 * #%L
 * pro.taskana:taskana-common
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
