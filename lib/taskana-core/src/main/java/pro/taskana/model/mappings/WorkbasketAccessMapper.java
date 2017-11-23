package pro.taskana.model.mappings;

import org.apache.ibatis.annotations.*;
import pro.taskana.model.WorkbasketAccessItem;

import java.util.List;
/**
 * This class is the mybatis mapping of workbasket access items.
 */
public interface WorkbasketAccessMapper {

    @Select("SELECT ID, WORKBASKET_ID, ACCESS_ID, PERM_READ, PERM_OPEN, PERM_APPEND, PERM_TRANSFER, PERM_DISTRIBUTE, PERM_CUSTOM_1, PERM_CUSTOM_2, PERM_CUSTOM_3, PERM_CUSTOM_4, PERM_CUSTOM_5, PERM_CUSTOM_6, PERM_CUSTOM_7, PERM_CUSTOM_8 FROM WORKBASKET_ACCESS_LIST WHERE ID = #{id}")
    @Results(value = {
            @Result(property = "id", column = "ID"),
            @Result(property = "workbasketId", column = "WORKBASKET_ID"),
            @Result(property = "accessId", column = "ACCESS_ID"),
            @Result(property = "permRead", column = "PERM_READ"),
            @Result(property = "permOpen", column = "PERM_OPEN"),
            @Result(property = "permAppend", column = "PERM_APPEND"),
            @Result(property = "permTransfer", column = "PERM_TRANSFER"),
            @Result(property = "permDistribute", column = "PERM_DISTRIBUTE"),
            @Result(property = "permCustom1", column = "PERM_CUSTOM_1"),
            @Result(property = "permCustom2", column = "PERM_CUSTOM_2"),
            @Result(property = "permCustom3", column = "PERM_CUSTOM_3"),
            @Result(property = "permCustom4", column = "PERM_CUSTOM_4"),
            @Result(property = "permCustom5", column = "PERM_CUSTOM_5"),
            @Result(property = "permCustom6", column = "PERM_CUSTOM_6"),
            @Result(property = "permCustom7", column = "PERM_CUSTOM_7"),
            @Result(property = "permCustom8", column = "PERM_CUSTOM_8")})
    WorkbasketAccessItem findById(@Param("id") String id);

    @Select("SELECT ID, WORKBASKET_ID, ACCESS_ID, PERM_READ, PERM_OPEN, PERM_APPEND, PERM_TRANSFER, PERM_DISTRIBUTE, PERM_CUSTOM_1, PERM_CUSTOM_2, PERM_CUSTOM_3, PERM_CUSTOM_4, PERM_CUSTOM_5, PERM_CUSTOM_6, PERM_CUSTOM_7, PERM_CUSTOM_8 FROM WORKBASKET_ACCESS_LIST WHERE ACCESS_ID = #{accessId}")
    @Results(value = {
            @Result(property = "id", column = "ID"),
            @Result(property = "workbasketId", column = "WORKBASKET_ID"),
            @Result(property = "accessId", column = "ACCESS_ID"),
            @Result(property = "permRead", column = "PERM_READ"),
            @Result(property = "permOpen", column = "PERM_OPEN"),
            @Result(property = "permAppend", column = "PERM_APPEND"),
            @Result(property = "permTransfer", column = "PERM_TRANSFER"),
            @Result(property = "permDistribute", column = "PERM_DISTRIBUTE"),
            @Result(property = "permCustom1", column = "PERM_CUSTOM_1"),
            @Result(property = "permCustom2", column = "PERM_CUSTOM_2"),
            @Result(property = "permCustom3", column = "PERM_CUSTOM_3"),
            @Result(property = "permCustom4", column = "PERM_CUSTOM_4"),
            @Result(property = "permCustom5", column = "PERM_CUSTOM_5"),
            @Result(property = "permCustom6", column = "PERM_CUSTOM_6"),
            @Result(property = "permCustom7", column = "PERM_CUSTOM_7"),
            @Result(property = "permCustom8", column = "PERM_CUSTOM_8")})
    List<WorkbasketAccessItem> findByAccessId(@Param("accessId") String accessId);

    @Select("SELECT ID, WORKBASKET_ID, ACCESS_ID, PERM_READ, PERM_OPEN, PERM_APPEND, PERM_TRANSFER, PERM_DISTRIBUTE, PERM_CUSTOM_1, PERM_CUSTOM_2, PERM_CUSTOM_3, PERM_CUSTOM_4, PERM_CUSTOM_5, PERM_CUSTOM_6, PERM_CUSTOM_7, PERM_CUSTOM_8 FROM WORKBASKET_ACCESS_LIST WHERE WORKBASKET_ID = #{id}")
    @Results(value = {
            @Result(property = "id", column = "ID"),
            @Result(property = "workbasketId", column = "WORKBASKET_ID"),
            @Result(property = "accessId", column = "ACCESS_ID"),
            @Result(property = "permRead", column = "PERM_READ"),
            @Result(property = "permOpen", column = "PERM_OPEN"),
            @Result(property = "permAppend", column = "PERM_APPEND"),
            @Result(property = "permTransfer", column = "PERM_TRANSFER"),
            @Result(property = "permDistribute", column = "PERM_DISTRIBUTE"),
            @Result(property = "permCustom1", column = "PERM_CUSTOM_1"),
            @Result(property = "permCustom2", column = "PERM_CUSTOM_2"),
            @Result(property = "permCustom3", column = "PERM_CUSTOM_3"),
            @Result(property = "permCustom4", column = "PERM_CUSTOM_4"),
            @Result(property = "permCustom5", column = "PERM_CUSTOM_5"),
            @Result(property = "permCustom6", column = "PERM_CUSTOM_6"),
            @Result(property = "permCustom7", column = "PERM_CUSTOM_7"),
            @Result(property = "permCustom8", column = "PERM_CUSTOM_8")})
    List<WorkbasketAccessItem> findByWorkbasketId(@Param("id") String id);

    @Select("SELECT ID, WORKBASKET_ID, ACCESS_ID, PERM_READ, PERM_OPEN, PERM_APPEND, PERM_TRANSFER, PERM_DISTRIBUTE, PERM_CUSTOM_1, PERM_CUSTOM_2, PERM_CUSTOM_3, PERM_CUSTOM_4, PERM_CUSTOM_5, PERM_CUSTOM_6, PERM_CUSTOM_7, PERM_CUSTOM_8 FROM WORKBASKET_ACCESS_LIST ORDER BY ID")
    @Results(value = {
            @Result(property = "id", column = "ID"),
            @Result(property = "workbasketId", column = "WORKBASKET_ID"),
            @Result(property = "accessId", column = "ACCESS_ID"),
            @Result(property = "permRead", column = "PERM_READ"),
            @Result(property = "permOpen", column = "PERM_OPEN"),
            @Result(property = "permAppend", column = "PERM_APPEND"),
            @Result(property = "permTransfer", column = "PERM_TRANSFER"),
            @Result(property = "permDistribute", column = "PERM_DISTRIBUTE"),
            @Result(property = "permCustom1", column = "PERM_CUSTOM_1"),
            @Result(property = "permCustom2", column = "PERM_CUSTOM_2"),
            @Result(property = "permCustom3", column = "PERM_CUSTOM_3"),
            @Result(property = "permCustom4", column = "PERM_CUSTOM_4"),
            @Result(property = "permCustom5", column = "PERM_CUSTOM_5"),
            @Result(property = "permCustom6", column = "PERM_CUSTOM_6"),
            @Result(property = "permCustom7", column = "PERM_CUSTOM_7"),
            @Result(property = "permCustom8", column = "PERM_CUSTOM_8")})
    List<WorkbasketAccessItem> findAll();

    @Insert("INSERT INTO WORKBASKET_ACCESS_LIST (ID, WORKBASKET_ID, ACCESS_ID, PERM_READ, PERM_OPEN, PERM_APPEND, PERM_TRANSFER, PERM_DISTRIBUTE, PERM_CUSTOM_1, PERM_CUSTOM_2, PERM_CUSTOM_3, PERM_CUSTOM_4, PERM_CUSTOM_5, PERM_CUSTOM_6, PERM_CUSTOM_7, PERM_CUSTOM_8) "
            + "VALUES (#{workbasketAccessItem.id}, #{workbasketAccessItem.workbasketId}, #{workbasketAccessItem.accessId}, #{workbasketAccessItem.permRead}, #{workbasketAccessItem.permOpen}, #{workbasketAccessItem.permAppend}, #{workbasketAccessItem.permTransfer}, #{workbasketAccessItem.permDistribute}, #{workbasketAccessItem.permCustom1}, #{workbasketAccessItem.permCustom2}, #{workbasketAccessItem.permCustom3}, #{workbasketAccessItem.permCustom4}, #{workbasketAccessItem.permCustom5}, #{workbasketAccessItem.permCustom6}, #{workbasketAccessItem.permCustom7}, #{workbasketAccessItem.permCustom8})")
    @Options(keyProperty = "id", keyColumn = "ID")
    void insert(@Param("workbasketAccessItem") WorkbasketAccessItem workbasketAccessItem);

    @Update("UPDATE WORKBASKET_ACCESS_LIST SET WORKBASKET_ID = #{workbasketAccessItem.workbasketId}, ACCESS_ID = #{workbasketAccessItem.accessId}, PERM_READ = #{workbasketAccessItem.permRead}, PERM_OPEN = #{workbasketAccessItem.permOpen}, PERM_APPEND = #{workbasketAccessItem.permAppend}, PERM_TRANSFER = #{workbasketAccessItem.permTransfer}, PERM_DISTRIBUTE = #{workbasketAccessItem.permDistribute}, PERM_CUSTOM_1 = #{workbasketAccessItem.permCustom1}, PERM_CUSTOM_2 = #{workbasketAccessItem.permCustom2}, PERM_CUSTOM_3 = #{workbasketAccessItem.permCustom3}, PERM_CUSTOM_4 = #{workbasketAccessItem.permCustom4}, PERM_CUSTOM_5 = #{workbasketAccessItem.permCustom5}, PERM_CUSTOM_6 = #{workbasketAccessItem.permCustom6}, PERM_CUSTOM_7 = #{workbasketAccessItem.permCustom7}, PERM_CUSTOM_8 = #{workbasketAccessItem.permCustom8} "
            + "WHERE id = #{workbasketAccessItem.id}")
    void update(@Param("workbasketAccessItem") WorkbasketAccessItem workbasketAccessItem);

    @Delete("DELETE FROM WORKBASKET_ACCESS_LIST where id = #{id}")
    void delete(@Param("id") String id);

    @Select("<script>SELECT ID, WORKBASKET_ID, ACCESS_ID, PERM_READ, PERM_OPEN, PERM_APPEND, PERM_TRANSFER, PERM_DISTRIBUTE, PERM_CUSTOM_1, PERM_CUSTOM_2, PERM_CUSTOM_3, PERM_CUSTOM_4, PERM_CUSTOM_5, PERM_CUSTOM_6, PERM_CUSTOM_7, PERM_CUSTOM_8 "
            + "FROM WORKBASKET_ACCESS_LIST "
            + "WHERE WORKBASKET_ID = #{workbasketId} "
            + "AND ACCESS_ID IN(<foreach item='item' collection='accessIds' separator=',' >#{item}</foreach>)"
            + "AND <if test=\"authorization == 'OPEN'\">PERM_OPEN</if>"
            + "<if test=\"authorization == 'READ'\">PERM_READ</if>"
            + "<if test=\"authorization == 'APPEND'\">PERM_APPEND</if>"
            + "<if test=\"authorization == 'TRANSFER'\">PERM_TRANSFER</if>"
            + "<if test=\"authorization == 'DISTRIBUTE'\">PERM_DISTRIBUTE</if> = 1</script>")
    @Results(value = {
            @Result(property = "id", column = "ID"),
            @Result(property = "workbasketId", column = "WORKBASKET_ID"),
            @Result(property = "accessId", column = "ACCESS_ID"),
            @Result(property = "permRead", column = "PERM_READ"),
            @Result(property = "permOpen", column = "PERM_OPEN"),
            @Result(property = "permAppend", column = "PERM_APPEND"),
            @Result(property = "permTransfer", column = "PERM_TRANSFER"),
            @Result(property = "permDistribute", column = "PERM_DISTRIBUTE"),
            @Result(property = "permCustom1", column = "PERM_CUSTOM_1"),
            @Result(property = "permCustom2", column = "PERM_CUSTOM_2"),
            @Result(property = "permCustom3", column = "PERM_CUSTOM_3"),
            @Result(property = "permCustom4", column = "PERM_CUSTOM_4"),
            @Result(property = "permCustom5", column = "PERM_CUSTOM_5"),
            @Result(property = "permCustom6", column = "PERM_CUSTOM_6"),
            @Result(property = "permCustom7", column = "PERM_CUSTOM_7"),
            @Result(property = "permCustom8", column = "PERM_CUSTOM_8")})
    List<WorkbasketAccessItem> findByWorkbasketAndAccessIdAndAuthorizations(@Param("workbasketId") String workbasketId, @Param("accessIds") List<String> accessIds, @Param("authorization") String authorization);

}
