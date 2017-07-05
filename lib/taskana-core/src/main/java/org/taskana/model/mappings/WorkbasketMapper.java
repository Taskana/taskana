package org.taskana.model.mappings;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.FetchType;
import org.taskana.model.Workbasket;
import org.taskana.model.WorkbasketAuthorization;
/**
 * This class is the mybatis mapping of workbaskets.
 */
public interface WorkbasketMapper {

    @Select("SELECT ID, TENANT_ID, CREATED, MODIFIED, NAME, DESCRIPTION, OWNER FROM WORKBASKET WHERE ID = #{id}")
    @Results(value = { @Result(property = "id", column = "ID"),
            @Result(property = "tenantId", column = "TENANT_ID"),
            @Result(property = "created", column = "CREATED"),
            @Result(property = "modified", column = "MODIFIED"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "description", column = "DESCRIPTION"),
            @Result(property = "owner", column = "OWNER"),
            @Result(property = "distributionTargets", column = "ID", javaType = List.class, many = @Many(fetchType = FetchType.DEFAULT, select = "findByDistributionTargets")) })
    Workbasket findById(@Param("id") String id);

    @Select("SELECT * FROM WORKBASKET WHERE id IN (SELECT TARGET_ID FROM DISTRIBUTION_TARGETS WHERE SOURCE_ID = #{id})")
    @Results(value = {
            @Result(property = "id", column = "ID"),
            @Result(property = "tenantId", column = "TENANT_ID"),
            @Result(property = "created", column = "CREATED"),
            @Result(property = "modified", column = "MODIFIED"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "description", column = "DESCRIPTION"),
            @Result(property = "owner", column = "OWNER"),
            @Result(property = "distributionTargets", column = "ID", javaType = List.class, many = @Many(fetchType = FetchType.DEFAULT, select = "findByDistributionTargets")) })
    List<Workbasket> findByDistributionTargets(@Param("id") String id);

    @Select("SELECT * FROM WORKBASKET ORDER BY id")
    @Results(value = {
            @Result(property = "id", column = "ID"),
            @Result(property = "tenantId", column = "TENANT_ID"),
            @Result(property = "created", column = "CREATED"),
            @Result(property = "modified", column = "MODIFIED"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "description", column = "DESCRIPTION"),
            @Result(property = "owner", column = "OWNER"),
            @Result(property = "distributionTargets", column = "ID", javaType = List.class, many = @Many(fetchType = FetchType.DEFAULT, select = "findByDistributionTargets")) })
    List<Workbasket> findAll();

    @Select("<script>SELECT W.ID, W.TENANT_ID, W.CREATED, W.MODIFIED, W.NAME, W.DESCRIPTION, W.OWNER FROM WORKBASKET AS W "
            + "INNER JOIN WORKBASKET_ACCESS_LIST AS ACL " + "ON (W.ID = ACL.WORKBASKET_ID AND USER_ID = #{userId}) "
            + "WHERE <foreach collection='authorizations' item='authorization' separator=' AND '>"
            + "<if test=\"authorization.name() == 'OPEN'\">OPEN</if>"
            + "<if test=\"authorization.name() == 'READ'\">READ</if>"
            + "<if test=\"authorization.name() == 'APPEND'\">APPEND</if>"
            + "<if test=\"authorization.name() == 'TRANSFER'\">TRANSFER</if>"
            + "<if test=\"authorization.name() == 'DISTRIBUTE'\">DISTRIBUTE</if> = 1 </foreach> "
            + "ORDER BY id</script>")
    @Results(value = {
            @Result(property = "id", column = "ID"),
            @Result(property = "tenantId", column = "TENANT_ID"),
            @Result(property = "created", column = "CREATED"),
            @Result(property = "modified", column = "MODIFIED"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "description", column = "DESCRIPTION"),
            @Result(property = "owner", column = "OWNER"),
            @Result(property = "distributionTargets", column = "ID", javaType = List.class, many = @Many(fetchType = FetchType.DEFAULT, select = "findByDistributionTargets")) })
    List<Workbasket> findByPermission(@Param("authorizations") List<WorkbasketAuthorization> authorizations, @Param("userId") String userId);

    @Insert("INSERT INTO WORKBASKET (ID, TENANT_ID, CREATED, MODIFIED, NAME, DESCRIPTION, OWNER) VALUES (#{workbasket.id}, #{workbasket.tenantId}, #{workbasket.created}, #{workbasket.modified}, #{workbasket.name}, #{workbasket.description}, #{workbasket.owner})")
    @Options(keyProperty = "id", keyColumn = "ID")
    void insert(@Param("workbasket") Workbasket workbasket);

    @Update("UPDATE WORKBASKET SET TENANT_ID = #{workbasket.tenantId}, MODIFIED = #{workbasket.modified}, NAME = #{workbasket.name}, DESCRIPTION = #{workbasket.description}, OWNER = #{workbasket.owner} WHERE id = #{workbasket.id}")
    void update(@Param("workbasket") Workbasket workbasket);

    @Delete("DELETE FROM WORKBASKET where id = #{id}")
    void delete(@Param("id") String id);

}
