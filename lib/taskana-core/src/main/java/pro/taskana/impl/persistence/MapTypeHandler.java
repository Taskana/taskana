package pro.taskana.impl.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
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
            // Convert Map to byte array
            try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
                ObjectOutputStream out = new ObjectOutputStream(byteOut);
                out.writeObject(parameter);
                ps.setBlob(i, new ByteArrayInputStream(byteOut.toByteArray()));
                out.close();
            } catch (IOException e) {
                LOGGER.error("During serialization of 'customAttributes' an error occured: ", e);
            }
        } else {
            ps.setNull(i, Types.BLOB);
        }
    }

    @Override
    public Map getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Blob fieldValue = rs.getBlob(columnName);
        if (fieldValue != null) {
            // Parse byte array to Map
            Map result = null;
            try (ObjectInputStream in = new ObjectInputStream(fieldValue.getBinaryStream())) {
                result = (Map) in.readObject();
            } catch (ClassNotFoundException | IOException e) {
                LOGGER.error("During deserialization of 'customAttributes' an error occured: ", e);
            }
            return result;
        }
        return null;
    }

    @Override
    public Map getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Blob fieldValue = rs.getBlob(columnIndex);
        if (fieldValue != null) {
            // Parse byte array to Map
            Map result = null;
            try (ObjectInputStream in = new ObjectInputStream(fieldValue.getBinaryStream())) {
                result = (Map) in.readObject();
            } catch (ClassNotFoundException | IOException e) {
                LOGGER.error("During deserialization of 'customAttributes' an error occured: ", e);
            }
            return result;
        }
        return null;
    }

    @Override
    public Map getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Blob fieldValue = cs.getBlob(columnIndex);
        if (fieldValue != null) {
            // Parse byte array to Map
            Map result = null;
            try (ObjectInputStream in = new ObjectInputStream(fieldValue.getBinaryStream())) {
                result = (Map) in.readObject();
            } catch (ClassNotFoundException | IOException e) {
                LOGGER.error("During deserialization of 'customAttributes' an error occured: ", e);
            }
            return result;
        }
        return null;
    }
}
