package pro.taskana.model.mappings;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import pro.taskana.impl.WorkbasketAccessItemImpl;

/**
 * This class is the mybatis mapping of workbasket access items.
 */
public interface WorkbasketAccessMapper {

    @Select("SELECT ID, WORKBASKET_KEY, ACCESS_ID, PERM_READ, PERM_OPEN, PERM_APPEND, PERM_TRANSFER, PERM_DISTRIBUTE, PERM_CUSTOM_1, PERM_CUSTOM_2, PERM_CUSTOM_3, PERM_CUSTOM_4, PERM_CUSTOM_5, PERM_CUSTOM_6, PERM_CUSTOM_7, PERM_CUSTOM_8, PERM_CUSTOM_9, PERM_CUSTOM_10, PERM_CUSTOM_11, PERM_CUSTOM_12 "
        + "FROM WORKBASKET_ACCESS_LIST WHERE ID = #{id}")
    @Results(value = {
        @Result(property = "id", column = "ID"),
        @Result(property = "workbasketKey", column = "WORKBASKET_KEY"),
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
        @Result(property = "permCustom8", column = "PERM_CUSTOM_8"),
        @Result(property = "permCustom9", column = "PERM_CUSTOM_9"),
        @Result(property = "permCustom10", column = "PERM_CUSTOM_10"),
        @Result(property = "permCustom11", column = "PERM_CUSTOM_11"),
        @Result(property = "permCustom12", column = "PERM_CUSTOM_12")})
    WorkbasketAccessItemImpl findById(@Param("id") String id);

    @Select("SELECT ID, WORKBASKET_KEY, ACCESS_ID, PERM_READ, PERM_OPEN, PERM_APPEND, PERM_TRANSFER, PERM_DISTRIBUTE, PERM_CUSTOM_1, PERM_CUSTOM_2, PERM_CUSTOM_3, PERM_CUSTOM_4, PERM_CUSTOM_5, PERM_CUSTOM_6, PERM_CUSTOM_7, PERM_CUSTOM_8, PERM_CUSTOM_9, PERM_CUSTOM_10, PERM_CUSTOM_11, PERM_CUSTOM_12 "
        + "FROM WORKBASKET_ACCESS_LIST WHERE ACCESS_ID = #{accessId}")
    @Results(value = {
        @Result(property = "id", column = "ID"),
        @Result(property = "workbasketKey", column = "WORKBASKET_KEY"),
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
        @Result(property = "permCustom8", column = "PERM_CUSTOM_8"),
        @Result(property = "permCustom9", column = "PERM_CUSTOM_9"),
        @Result(property = "permCustom10", column = "PERM_CUSTOM_10"),
        @Result(property = "permCustom11", column = "PERM_CUSTOM_11"),
        @Result(property = "permCustom12", column = "PERM_CUSTOM_12")})
    List<WorkbasketAccessItemImpl> findByAccessId(@Param("accessId") String accessId);

    @Select("SELECT ID, WORKBASKET_KEY, ACCESS_ID, PERM_READ, PERM_OPEN, PERM_APPEND, PERM_TRANSFER, PERM_DISTRIBUTE, PERM_CUSTOM_1, PERM_CUSTOM_2, PERM_CUSTOM_3, PERM_CUSTOM_4, PERM_CUSTOM_5, PERM_CUSTOM_6, PERM_CUSTOM_7, PERM_CUSTOM_8, PERM_CUSTOM_9, PERM_CUSTOM_10, PERM_CUSTOM_11, PERM_CUSTOM_12 "
        + "FROM WORKBASKET_ACCESS_LIST WHERE WORKBASKET_KEY = #{key}")
    @Results(value = {
        @Result(property = "id", column = "ID"),
        @Result(property = "workbasketKey", column = "WORKBASKET_KEY"),
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
        @Result(property = "permCustom8", column = "PERM_CUSTOM_8"),
        @Result(property = "permCustom9", column = "PERM_CUSTOM_9"),
        @Result(property = "permCustom10", column = "PERM_CUSTOM_10"),
        @Result(property = "permCustom11", column = "PERM_CUSTOM_11"),
        @Result(property = "permCustom12", column = "PERM_CUSTOM_12")})
    List<WorkbasketAccessItemImpl> findByWorkbasketKey(@Param("key") String key);

    @Insert("INSERT INTO WORKBASKET_ACCESS_LIST (ID, WORKBASKET_KEY, ACCESS_ID, PERM_READ, PERM_OPEN, PERM_APPEND, PERM_TRANSFER, PERM_DISTRIBUTE,  PERM_CUSTOM_1, PERM_CUSTOM_2, PERM_CUSTOM_3, PERM_CUSTOM_4, PERM_CUSTOM_5, PERM_CUSTOM_6, PERM_CUSTOM_7, PERM_CUSTOM_8, PERM_CUSTOM_9, PERM_CUSTOM_10, PERM_CUSTOM_11, PERM_CUSTOM_12) "
        + "VALUES (#{workbasketAccessItem.id}, #{workbasketAccessItem.workbasketKey}, #{workbasketAccessItem.accessId}, #{workbasketAccessItem.permRead}, #{workbasketAccessItem.permOpen}, #{workbasketAccessItem.permAppend}, #{workbasketAccessItem.permTransfer}, #{workbasketAccessItem.permDistribute}, #{workbasketAccessItem.permCustom1}, #{workbasketAccessItem.permCustom2}, #{workbasketAccessItem.permCustom3}, #{workbasketAccessItem.permCustom4}, #{workbasketAccessItem.permCustom5}, #{workbasketAccessItem.permCustom6}, #{workbasketAccessItem.permCustom7}, #{workbasketAccessItem.permCustom8}, #{workbasketAccessItem.permCustom9}, #{workbasketAccessItem.permCustom10}, #{workbasketAccessItem.permCustom11}, #{workbasketAccessItem.permCustom12})")
    @Options(keyProperty = "id", keyColumn = "ID")
    void insert(@Param("workbasketAccessItem") WorkbasketAccessItemImpl workbasketAccessItem);

    @Update("UPDATE WORKBASKET_ACCESS_LIST SET WORKBASKET_KEY = #{workbasketAccessItem.workbasketKey}, ACCESS_ID = #{workbasketAccessItem.accessId}, PERM_READ = #{workbasketAccessItem.permRead}, PERM_OPEN = #{workbasketAccessItem.permOpen}, PERM_APPEND = #{workbasketAccessItem.permAppend}, PERM_TRANSFER = #{workbasketAccessItem.permTransfer}, PERM_DISTRIBUTE = #{workbasketAccessItem.permDistribute}, PERM_CUSTOM_1 = #{workbasketAccessItem.permCustom1}, PERM_CUSTOM_2 = #{workbasketAccessItem.permCustom2}, PERM_CUSTOM_3 = #{workbasketAccessItem.permCustom3}, PERM_CUSTOM_4 = #{workbasketAccessItem.permCustom4}, PERM_CUSTOM_5 = #{workbasketAccessItem.permCustom5}, PERM_CUSTOM_6 = #{workbasketAccessItem.permCustom6}, PERM_CUSTOM_7 = #{workbasketAccessItem.permCustom7}, PERM_CUSTOM_8 = #{workbasketAccessItem.permCustom8}, PERM_CUSTOM_9 = #{workbasketAccessItem.permCustom9}, PERM_CUSTOM_10 = #{workbasketAccessItem.permCustom10}, PERM_CUSTOM_11 = #{workbasketAccessItem.permCustom11}, PERM_CUSTOM_12 = #{workbasketAccessItem.permCustom12} "
        + "WHERE id = #{workbasketAccessItem.id}")
    void update(@Param("workbasketAccessItem") WorkbasketAccessItemImpl workbasketAccessItem);

    @Delete("DELETE FROM WORKBASKET_ACCESS_LIST where id = #{id}")
    void delete(@Param("id") String id);

    @Delete("DELETE FROM WORKBASKET_ACCESS_LIST where WORKBASKET_KEY = #{workbasketKey}")
    void deleteAllForWorkbasketkey(@Param("workbasketKey") String workbasketKey);

    @Select("<script>SELECT MAX(PERM_READ) AS P_READ, MAX(PERM_OPEN) AS P_OPEN, MAX(PERM_APPEND) AS P_APPEND, MAX(PERM_TRANSFER) AS P_TRANSFER, MAX(PERM_DISTRIBUTE) AS P_DISTRIBUTE, MAX(PERM_CUSTOM_1) AS P_CUSTOM_1, MAX(PERM_CUSTOM_2) AS P_CUSTOM_2, MAX(PERM_CUSTOM_3) AS P_CUSTOM_3, MAX(PERM_CUSTOM_4) AS P_CUSTOM_4, MAX(PERM_CUSTOM_5) AS P_CUSTOM_5, MAX(PERM_CUSTOM_6) AS P_CUSTOM_6, MAX(PERM_CUSTOM_7) AS P_CUSTOM_7, MAX(PERM_CUSTOM_8) AS P_CUSTOM_8, MAX(PERM_CUSTOM_9) AS P_CUSTOM_9, MAX(PERM_CUSTOM_10) AS P_CUSTOM_10, MAX(PERM_CUSTOM_11) AS P_CUSTOM_11, MAX(PERM_CUSTOM_12) AS P_CUSTOM_12 "
        + "FROM WORKBASKET_ACCESS_LIST "
        + "WHERE WORKBASKET_KEY = #{workbasketKey} "
        + "AND ACCESS_ID IN(<foreach item='item' collection='accessIds' separator=',' >#{item}</foreach>)"
        + "</script>")
    @Results(value = {
        @Result(property = "permRead", column = "P_READ"),
        @Result(property = "permOpen", column = "P_OPEN"),
        @Result(property = "permAppend", column = "P_APPEND"),
        @Result(property = "permTransfer", column = "P_TRANSFER"),
        @Result(property = "permDistribute", column = "P_DISTRIBUTE"),
        @Result(property = "permCustom1", column = "P_CUSTOM_1"),
        @Result(property = "permCustom2", column = "P_CUSTOM_2"),
        @Result(property = "permCustom3", column = "P_CUSTOM_3"),
        @Result(property = "permCustom4", column = "P_CUSTOM_4"),
        @Result(property = "permCustom5", column = "P_CUSTOM_5"),
        @Result(property = "permCustom6", column = "P_CUSTOM_6"),
        @Result(property = "permCustom7", column = "P_CUSTOM_7"),
        @Result(property = "permCustom8", column = "P_CUSTOM_8"),
        @Result(property = "permCustom9", column = "P_CUSTOM_9"),
        @Result(property = "permCustom10", column = "P_CUSTOM_10"),
        @Result(property = "permCustom11", column = "P_CUSTOM_11"),
        @Result(property = "permCustom12", column = "P_CUSTOM_12")})
    WorkbasketAccessItemImpl findByWorkbasketAndAccessId(
        @Param("workbasketKey") String workbasketKey, @Param("accessIds") List<String> accessIds);

    @Select("<script>SELECT ID, WORKBASKET_KEY, ACCESS_ID, PERM_READ, PERM_OPEN, PERM_APPEND, PERM_TRANSFER, PERM_DISTRIBUTE, PERM_CUSTOM_1, PERM_CUSTOM_2, PERM_CUSTOM_3, PERM_CUSTOM_4, PERM_CUSTOM_5, PERM_CUSTOM_6, PERM_CUSTOM_7, PERM_CUSTOM_8, PERM_CUSTOM_9, PERM_CUSTOM_10, PERM_CUSTOM_11, PERM_CUSTOM_12 "
        + "FROM WORKBASKET_ACCESS_LIST "
        + "WHERE WORKBASKET_KEY = #{workbasketKey} "
        + "AND ACCESS_ID IN(<foreach item='item' collection='accessIds' separator=',' >#{item}</foreach>)"
        + "AND <if test=\"authorization == 'OPEN'\">PERM_OPEN</if>"
        + "<if test=\"authorization == 'READ'\">PERM_READ</if>"
        + "<if test=\"authorization == 'APPEND'\">PERM_APPEND</if>"
        + "<if test=\"authorization == 'TRANSFER'\">PERM_TRANSFER</if>"
        + "<if test=\"authorization == 'DISTRIBUTE'\">PERM_DISTRIBUTE</if>"
        + "<if test=\"authorization == 'CUSTOM_1'\">PERM_CUSTOM_1</if>"
        + "<if test=\"authorization == 'CUSTOM_2'\">PERM_CUSTOM_2</if>"
        + "<if test=\"authorization == 'CUSTOM_3'\">PERM_CUSTOM_3</if>"
        + "<if test=\"authorization == 'CUSTOM_4'\">PERM_CUSTOM_4</if>"
        + "<if test=\"authorization == 'CUSTOM_5'\">PERM_CUSTOM_5</if>"
        + "<if test=\"authorization == 'CUSTOM_6'\">PERM_CUSTOM_6</if>"
        + "<if test=\"authorization == 'CUSTOM_7'\">PERM_CUSTOM_7</if>"
        + "<if test=\"authorization == 'CUSTOM_8'\">PERM_CUSTOM_8</if>"
        + "<if test=\"authorization == 'CUSTOM_9'\">PERM_CUSTOM_9</if>"
        + "<if test=\"authorization == 'CUSTOM_10'\">PERM_CUSTOM_10</if>"
        + "<if test=\"authorization == 'CUSTOM_11'\">PERM_CUSTOM_11</if>"
        + "<if test=\"authorization == 'CUSTOM_12'\">PERM_CUSTOM_12</if> = 1</script>")
    @Results(value = {
        @Result(property = "id", column = "ID"),
        @Result(property = "workbasketKey", column = "WORKBASKET_KEY"),
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
        @Result(property = "permCustom8", column = "PERM_CUSTOM_8"),
        @Result(property = "permCustom9", column = "PERM_CUSTOM_9"),
        @Result(property = "permCustom10", column = "PERM_CUSTOM_10"),
        @Result(property = "permCustom11", column = "PERM_CUSTOM_11"),
        @Result(property = "permCustom12", column = "PERM_CUSTOM_12")})
    List<WorkbasketAccessItemImpl> findByWorkbasketAndAccessIdAndAuthorization(
        @Param("workbasketKey") String workbasketKey, @Param("accessIds") List<String> accessIds,
        @Param("authorization") String authorization);

    @Select("<script>SELECT A.ID, A.WORKBASKET_KEY, A.ACCESS_ID, A.PERM_READ, A.PERM_OPEN, A.PERM_APPEND, A.PERM_TRANSFER, A.PERM_DISTRIBUTE, A.PERM_CUSTOM_1, A.PERM_CUSTOM_2, A.PERM_CUSTOM_3, A.PERM_CUSTOM_4, A.PERM_CUSTOM_5, A.PERM_CUSTOM_6, A.PERM_CUSTOM_7, A.PERM_CUSTOM_8, A.PERM_CUSTOM_9, A.PERM_CUSTOM_10, A.PERM_CUSTOM_11, A.PERM_CUSTOM_12 "
        + "FROM WORKBASKET_ACCESS_LIST AS A "
        + "LEFT JOIN WORKBASKET AS W ON A.WORKBASKET_KEY = W.KEY "
        + "WHERE W.ID = #{workbasketId} "
        + "AND A.ACCESS_ID IN(<foreach item='item' collection='accessIds' separator=',' >#{item}</foreach>)"
        + "AND <if test=\"authorization == 'OPEN'\">PERM_OPEN</if>"
        + "<if test=\"authorization == 'READ'\">PERM_READ</if>"
        + "<if test=\"authorization == 'APPEND'\">PERM_APPEND</if>"
        + "<if test=\"authorization == 'TRANSFER'\">PERM_TRANSFER</if>"
        + "<if test=\"authorization == 'DISTRIBUTE'\">PERM_DISTRIBUTE</if>"
        + "<if test=\"authorization == 'CUSTOM_1'\">PERM_CUSTOM_1</if>"
        + "<if test=\"authorization == 'CUSTOM_2'\">PERM_CUSTOM_2</if>"
        + "<if test=\"authorization == 'CUSTOM_3'\">PERM_CUSTOM_3</if>"
        + "<if test=\"authorization == 'CUSTOM_4'\">PERM_CUSTOM_4</if>"
        + "<if test=\"authorization == 'CUSTOM_5'\">PERM_CUSTOM_5</if>"
        + "<if test=\"authorization == 'CUSTOM_6'\">PERM_CUSTOM_6</if>"
        + "<if test=\"authorization == 'CUSTOM_7'\">PERM_CUSTOM_7</if>"
        + "<if test=\"authorization == 'CUSTOM_8'\">PERM_CUSTOM_8</if>"
        + "<if test=\"authorization == 'CUSTOM_9'\">PERM_CUSTOM_9</if>"
        + "<if test=\"authorization == 'CUSTOM_10'\">PERM_CUSTOM_10</if>"
        + "<if test=\"authorization == 'CUSTOM_11'\">PERM_CUSTOM_11</if>"
        + "<if test=\"authorization == 'CUSTOM_12'\">PERM_CUSTOM_12</if> = 1</script>")
    List<WorkbasketAccessItemImpl> findByWorkbasketAndAccessIdAndAuthorizationsById(
        @Param("workbasketId") String workbasketId, @Param("accessIds") List<String> accessIds,
        @Param("authorization") String authorization);
}
