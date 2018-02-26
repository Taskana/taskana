package pro.taskana.impl.persistence;

import java.sql.CallableStatement;
import java.sql.Clob;
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

/**
 * This Typehandler will transfer a Map into an xml blob and back.
 *
 * @author EH
 */
@SuppressWarnings("rawtypes")
public class MapTypeHandler extends BaseTypeHandler<Map> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapTypeHandler.class);

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map parameter, JdbcType jdbcType) throws SQLException {
        if (parameter != null && parameter.size() > 0) {
            LOGGER.debug("Input-Map before serializing: ", parameter);
            // Convert Map to JSON string
            JSONObject jsonObj = new JSONObject(parameter);
            Clob content = ps.getConnection().createClob();
            content.setString(1, jsonObj.toString());
            ps.setClob(i, content);
        } else {
            ps.setNull(i, Types.BLOB);
        }
    }

    @Override
    public Map getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Clob fieldValue = rs.getClob(columnName);
        if (fieldValue != null) {
            return convertClobToMap(fieldValue);
        }
        return null;
    }

    @Override
    public Map getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Clob fieldValue = rs.getClob(columnIndex);
        if (fieldValue != null) {
            return convertClobToMap(fieldValue);
        }
        return null;
    }

    @Override
    public Map getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Clob fieldValue = cs.getClob(columnIndex);
        if (fieldValue != null) {
            return convertClobToMap(fieldValue);
        }
        return null;
    }

    private Map convertClobToMap(Clob fieldValue) throws SQLException {
        String content = fieldValue.getSubString(1L, (int) fieldValue.length());
        JSONObject jsonObj = new JSONObject(content);
        Map result = jsonObj.toMap();
        return result;
    }

}
