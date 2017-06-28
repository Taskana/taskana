package org.taskana.model.mappings;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.taskana.model.WorkbasketAccessItem;

public interface WorkbasketAccessMapper {
    
    @Select("SELECT ID, WORKBASKET_ID, USER_ID, GROUP_ID, READ, OPEN, APPEND, TRANSFER, DISTRIBUTE FROM WORKBASKET_ACCESS_LIST WHERE ID = #{id}")
    @Results(value = { 
            @Result(property = "id", column = "ID"), 
            @Result(property = "workbasketId", column = "WORKBASKET_ID"),
            @Result(property = "userId", column = "USER_ID"),
            @Result(property = "groupId", column = "GROUP_ID"),
            @Result(property = "read", column = "READ"),
            @Result(property = "open", column = "OPEN"),
            @Result(property = "append", column = "APPEND"),
            @Result(property = "transfer", column = "TRANSFER"),
            @Result(property = "distribute", column = "DISTRIBUTE")})
    WorkbasketAccessItem findById(@Param("id") String id);
    
    @Select("SELECT ID, WORKBASKET_ID, USER_ID, GROUP_ID, READ, OPEN, APPEND, TRANSFER, DISTRIBUTE FROM WORKBASKET_ACCESS_LIST WHERE USER_ID = #{userId}")
    @Results(value = { 
            @Result(property = "id", column = "ID"), 
            @Result(property = "workbasketId", column = "WORKBASKET_ID"),
            @Result(property = "userId", column = "USER_ID"),
            @Result(property = "groupId", column = "GROUP_ID"),
            @Result(property = "read", column = "READ"),
            @Result(property = "open", column = "OPEN"),
            @Result(property = "append", column = "APPEND"),
            @Result(property = "transfer", column = "TRANSFER"),
            @Result(property = "distribute", column = "DISTRIBUTE")})
    List<WorkbasketAccessItem> findByUserId(@Param("userId") String userId);
    
    @Select("SELECT ID, WORKBASKET_ID, USER_ID, GROUP_ID, READ, OPEN, APPEND, TRANSFER, DISTRIBUTE FROM WORKBASKET_ACCESS_LIST WHERE WORKBASKET_ID = #{id}")
    @Results(value = { 
            @Result(property = "id", column = "ID"), 
            @Result(property = "workbasketId", column = "WORKBASKET_ID"),
            @Result(property = "userId", column = "USER_ID"),
            @Result(property = "groupId", column = "GROUP_ID"),
            @Result(property = "read", column = "READ"),
            @Result(property = "open", column = "OPEN"),
            @Result(property = "append", column = "APPEND"),
            @Result(property = "transfer", column = "TRANSFER"),
            @Result(property = "distribute", column = "DISTRIBUTE")})
    List<WorkbasketAccessItem> findByWorkbasketId(@Param("id") String id);
    
    @Select("SELECT ID, WORKBASKET_ID, USER_ID, GROUP_ID, READ, OPEN, APPEND, TRANSFER, DISTRIBUTE FROM WORKBASKET_ACCESS_LIST ORDER BY ID")
    @Results(value = { 
            @Result(property = "id", column = "ID"), 
            @Result(property = "workbasketId", column = "WORKBASKET_ID"),
            @Result(property = "userId", column = "USER_ID"),
            @Result(property = "groupId", column = "GROUP_ID"),
            @Result(property = "read", column = "READ"),
            @Result(property = "open", column = "OPEN"),
            @Result(property = "append", column = "APPEND"),
            @Result(property = "transfer", column = "TRANSFER"),
            @Result(property = "distribute", column = "DISTRIBUTE")})
    List<WorkbasketAccessItem> findAll();
    
    @Insert("INSERT INTO WORKBASKET_ACCESS_LIST (ID, WORKBASKET_ID, USER_ID, GROUP_ID, READ, OPEN, APPEND, TRANSFER, DISTRIBUTE) "
            + "VALUES (#{workbasketAccessItem.id}, #{workbasketAccessItem.workbasketId}, #{workbasketAccessItem.userId}, #{workbasketAccessItem.groupId}, #{workbasketAccessItem.read}, #{workbasketAccessItem.open}, #{workbasketAccessItem.append}, #{workbasketAccessItem.transfer}, #{workbasketAccessItem.distribute})")
    @Options(keyProperty = "id", keyColumn="ID")
    void insert(@Param("workbasketAccessItem") WorkbasketAccessItem workbasketAccessItem);
    
    @Update("UPDATE WORKBASKET_ACCESS_LIST SET WORKBASKET_ID = #{workbasketAccessItem.workbasketId}, USER_ID = #{workbasketAccessItem.userId}, GROUP_ID = #{workbasketAccessItem.groupId}, READ = #{workbasketAccessItem.read}, OPEN = #{workbasketAccessItem.open}, APPEND = #{workbasketAccessItem.append}, TRANSFER = #{workbasketAccessItem.transfer}, DISTRIBUTE = #{workbasketAccessItem.distribute} "
            + "WHERE id = #{workbasketAccessItem.id}")
    void update(@Param("workbasketAccessItem") WorkbasketAccessItem workbasketAccessItem);

    @Delete("DELETE FROM WORKBASKET_ACCESS_LIST where id = #{id}")
    void delete(@Param("id") String id);
    
    @Select("<script>SELECT ID, WORKBASKET_ID, USER_ID, GROUP_ID, READ, OPEN, APPEND, TRANSFER, DISTRIBUTE "
    		+ "FROM WORKBASKET_ACCESS_LIST "
    		+ "WHERE WORKBASKET_ID = #{workbasketId} "
    		+ "AND USER_ID = #{userId} "
    		+ "AND <if test=\"authorization == 'OPEN'\">OPEN</if>"
    		+ "<if test=\"authorization == 'READ'\">READ</if>"
    		+ "<if test=\"authorization == 'APPEND'\">APPEND</if>"
    		+ "<if test=\"authorization == 'TRANSFER'\">TRANSFER</if>"
    		+ "<if test=\"authorization == 'DISTRIBUTE'\">DISTRIBUTE</if> = 1</script>")
    @Results(value = { 
            @Result(property = "id", column = "ID"), 
            @Result(property = "workbasketId", column = "WORKBASKET_ID"),
            @Result(property = "userId", column = "USER_ID"),
            @Result(property = "groupId", column = "GROUP_ID"),
            @Result(property = "read", column = "READ"),
            @Result(property = "open", column = "OPEN"),
            @Result(property = "append", column = "APPEND"),
            @Result(property = "transfer", column = "TRANSFER"),
            @Result(property = "distribute", column = "DISTRIBUTE")})
    List<WorkbasketAccessItem> findByWorkbasketAndUserAndAuthorization(@Param("workbasketId") String workbasketId, @Param("userId") String userId, @Param("authorization") String authorization);
    
    @Select("SELECT ID, WORKBASKET_ID, USER_ID, GROUP_ID, READ, OPEN, APPEND, TRANSFER, DISTRIBUTE FROM WORKBASKET_ACCESS_LIST WHERE WORKBASKET_ID = #{workbasketId} AND GROUP_ID = #{groupId}")
    @Results(value = { 
            @Result(property = "id", column = "ID"), 
            @Result(property = "workbasketId", column = "WORKBASKET_ID"),
            @Result(property = "userId", column = "USER_ID"),
            @Result(property = "groupId", column = "GROUP_ID"),
            @Result(property = "read", column = "READ"),
            @Result(property = "open", column = "OPEN"),
            @Result(property = "append", column = "APPEND"),
            @Result(property = "transfer", column = "TRANSFER"),
            @Result(property = "distribute", column = "DISTRIBUTE")})
    List<WorkbasketAccessItem> findByWorkbasketAndGroup(@Param("workbasketId") String workbasketId, @Param("groupId") String groupId);
}
