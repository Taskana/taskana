package pro.taskana.task.internal;

import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import pro.taskana.task.internal.models.ObjectReferenceImpl;

/** This class is the mybatis mapping of ObjectReference. */
@SuppressWarnings({"checkstyle:LineLength", "checkstyle:Indentation"})
public interface ObjectReferenceMapper {

  @Select(
      "<script>SELECT ID, COMPANY, SYSTEM, SYSTEM_INSTANCE, TYPE, VALUE "
          + "FROM OBJECT_REFERENCE "
          + "WHERE ID = #{id}"
          + "ORDER BY VALUE ASC, TYPE ASC, SYSTEM_INSTANCE ASC, SYSTEM ASC, COMPANY ASC"
          + "<if test=\"_databaseId == 'db2'\"> with UR </if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "company", column = "COMPANY")
  @Result(property = "system", column = "SYSTEM")
  @Result(property = "systemInstance", column = "SYSTEM_INSTANCE")
  @Result(property = "type", column = "TYPE")
  @Result(property = "value", column = "VALUE")
  ObjectReferenceImpl findById(@Param("id") String id);

  @Select(
      "<script>SELECT ID, TASK_ID, COMPANY, SYSTEM, SYSTEM_INSTANCE, TYPE, VALUE "
          + "FROM OBJECT_REFERENCE "
          + "WHERE TASK_ID = #{taskId}"
          + " ORDER BY VALUE ASC, TYPE ASC, SYSTEM_INSTANCE ASC, SYSTEM ASC, COMPANY ASC"
          + "<if test=\"_databaseId == 'db2'\"> with UR </if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "taskId", column = "TASK_ID")
  @Result(property = "company", column = "COMPANY")
  @Result(property = "system", column = "SYSTEM")
  @Result(property = "systemInstance", column = "SYSTEM_INSTANCE")
  @Result(property = "type", column = "TYPE")
  @Result(property = "value", column = "VALUE")
  List<ObjectReferenceImpl> findObjectReferencesByTaskId(@Param("taskId") String taskId);

  @Select(
      "<script>SELECT ID, TASK_ID, COMPANY, SYSTEM, SYSTEM_INSTANCE, TYPE, VALUE "
          + "FROM OBJECT_REFERENCE "
          + "<where>"
          + "<choose>"
          + "<when  test='taskIds == null'>"
          + " 1 = 2 "
          + "</when>"
          + "<otherwise>"
          + "TASK_ID IN (<foreach collection='taskIds' item='item' separator=',' >#{item}</foreach>) "
          + "</otherwise>"
          + "</choose>"
          + "</where>"
          + "ORDER BY VALUE ASC, TYPE ASC, SYSTEM_INSTANCE ASC, SYSTEM ASC, COMPANY ASC"
          + "<if test=\"_databaseId == 'db2'\"> with UR </if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "taskId", column = "TASK_ID")
  @Result(property = "company", column = "COMPANY")
  @Result(property = "system", column = "SYSTEM")
  @Result(property = "systemInstance", column = "SYSTEM_INSTANCE")
  @Result(property = "type", column = "TYPE")
  @Result(property = "value", column = "VALUE")
  List<ObjectReferenceImpl> findObjectReferencesByTaskIds(
      @Param("taskIds") Collection<String> taskIds);

  @Select(
      "<script>SELECT ID, COMPANY, SYSTEM, SYSTEM_INSTANCE, TYPE, VALUE "
          + "FROM OBJECT_REFERENCE "
          + "WHERE COMPANY = #{objectReference.company} "
          + "AND SYSTEM = #{objectReference.system} "
          + "AND SYSTEM_INSTANCE = #{objectReference.systemInstance} "
          + "AND TYPE = #{objectReference.type} "
          + "AND VALUE = #{objectReference.value} "
          + "ORDER BY VALUE ASC, TYPE ASC, SYSTEM_INSTANCE ASC, SYSTEM ASC, COMPANY ASC"
          + "<if test=\"_databaseId == 'db2'\"> with UR </if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "company", column = "COMPANY")
  @Result(property = "system", column = "SYSTEM")
  @Result(property = "systemInstance", column = "SYSTEM_INSTANCE")
  @Result(property = "type", column = "TYPE")
  @Result(property = "value", column = "VALUE")
  ObjectReferenceImpl findByObjectReference(
      @Param("objectReference") ObjectReferenceImpl objectReference);

  @SelectProvider(type = ObjectReferenceQuerySqlProvider.class, method = "queryObjectReferences")
  @Result(property = "id", column = "ID")
  @Result(property = "company", column = "COMPANY")
  @Result(property = "system", column = "SYSTEM")
  @Result(property = "systemInstance", column = "SYSTEM_INSTANCE")
  @Result(property = "type", column = "TYPE")
  @Result(property = "value", column = "VALUE")
  List<ObjectReferenceImpl> queryObjectReferences(ObjectReferenceQueryImpl objectReference);

  @SelectProvider(
      type = ObjectReferenceQuerySqlProvider.class,
      method = "countQueryObjectReferences")
  long countQueryObjectReferences(ObjectReferenceQueryImpl objectReference);

  @SelectProvider(
      type = ObjectReferenceQuerySqlProvider.class,
      method = "queryObjectReferenceColumnValues")
  List<String> queryObjectReferenceColumnValues(ObjectReferenceQueryImpl objectReference);

  @Insert(
      "INSERT INTO OBJECT_REFERENCE (ID, TASK_ID, COMPANY, SYSTEM, SYSTEM_INSTANCE, TYPE, VALUE) "
          + "VALUES (#{ref.id}, #{ref.taskId}, #{ref.company}, #{ref.system}, #{ref.systemInstance}, #{ref.type}, #{ref.value})")
  void insert(@Param("ref") ObjectReferenceImpl ref);

  @Update(
      value =
          "UPDATE OBJECT_REFERENCE "
              + "SET COMPANY = #{ref.company}, SYSTEM = #{ref.system}, SYSTEM_INSTANCE = #{ref.systemInstance}, TYPE = #{ref.type}, VALUE = #{ref.value} "
              + "WHERE ID = #{ref.id}")
  void update(@Param("ref") ObjectReferenceImpl ref);

  @Delete("DELETE FROM OBJECT_REFERENCE WHERE ID = #{id}")
  void delete(String id);

  @Delete(
      "<script>DELETE FROM OBJECT_REFERENCE WHERE TASK_ID IN(<foreach item='item' collection='taskIds' separator=',' >#{item}</foreach>)</script>")
  void deleteMultipleByTaskIds(@Param("taskIds") List<String> taskIds);
}
