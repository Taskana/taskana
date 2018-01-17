package pro.taskana.model.mappings;

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

import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.WorkbasketSummaryImpl;
import pro.taskana.model.WorkbasketAuthorization;

/**
 * This class is the mybatis mapping of workbaskets.
 */
public interface WorkbasketMapper {

    @Select("SELECT ID, KEY, CREATED, MODIFIED, NAME, DOMAIN, TYPE, DESCRIPTION, OWNER, CUSTOM_1 ,CUSTOM_2 ,CUSTOM_3 ,CUSTOM_4 ,ORG_LEVEL_1 ,ORG_LEVEL_2 ,ORG_LEVEL_3 ,ORG_LEVEL_4 FROM WORKBASKET WHERE ID = #{id}")
    @Results(value = {@Result(property = "id", column = "ID"),
        @Result(property = "key", column = "KEY"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "modified", column = "MODIFIED"),
        @Result(property = "name", column = "NAME"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "type", column = "TYPE"),
        @Result(property = "description", column = "DESCRIPTION"),
        @Result(property = "owner", column = "OWNER"),
        @Result(property = "distributionTargets", column = "ID", javaType = List.class,
            many = @Many(fetchType = FetchType.DEFAULT, select = "findByDistributionTargets")),
        @Result(property = "custom1", column = "CUSTOM_1"),
        @Result(property = "custom2", column = "CUSTOM_2"),
        @Result(property = "custom3", column = "CUSTOM_3"),
        @Result(property = "custom4", column = "CUSTOM_4"),
        @Result(property = "orgLevel1", column = "ORG_LEVEL_1"),
        @Result(property = "orgLevel2", column = "ORG_LEVEL_2"),
        @Result(property = "orgLevel3", column = "ORG_LEVEL_3"),
        @Result(property = "orgLevel4", column = "ORG_LEVEL_4") })
    WorkbasketImpl findById(@Param("id") String id);

    @Select("SELECT ID, KEY, CREATED, MODIFIED, NAME, DOMAIN, TYPE, DESCRIPTION, OWNER, CUSTOM_1 ,CUSTOM_2 ,CUSTOM_3 ,CUSTOM_4 ,ORG_LEVEL_1 ,ORG_LEVEL_2 ,ORG_LEVEL_3 ,ORG_LEVEL_4 FROM WORKBASKET WHERE KEY = #{key}")
    @Results(value = {@Result(property = "id", column = "ID"),
        @Result(property = "key", column = "KEY"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "modified", column = "MODIFIED"),
        @Result(property = "name", column = "NAME"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "type", column = "TYPE"),
        @Result(property = "description", column = "DESCRIPTION"),
        @Result(property = "owner", column = "OWNER"),
        @Result(property = "distributionTargets", column = "ID", javaType = List.class,
            many = @Many(fetchType = FetchType.DEFAULT, select = "findByDistributionTargets")),
        @Result(property = "custom1", column = "CUSTOM_1"),
        @Result(property = "custom2", column = "CUSTOM_2"),
        @Result(property = "custom3", column = "CUSTOM_3"),
        @Result(property = "custom4", column = "CUSTOM_4"),

        @Result(property = "orgLevel1", column = "ORG_LEVEL_1"),
        @Result(property = "orgLevel2", column = "ORG_LEVEL_2"),
        @Result(property = "orgLevel3", column = "ORG_LEVEL_3"),
        @Result(property = "orgLevel4", column = "ORG_LEVEL_4") })
    WorkbasketImpl findByKey(@Param("key") String key);

    @Select("SELECT * FROM WORKBASKET WHERE id IN (SELECT TARGET_ID FROM DISTRIBUTION_TARGETS WHERE SOURCE_ID = #{id})")
    @Results(value = {
        @Result(property = "id", column = "ID"),
        @Result(property = "key", column = "KEY"),
        @Result(property = "name", column = "NAME"),
        @Result(property = "description", column = "DESCRIPTION"),
        @Result(property = "owner", column = "OWNER"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "type", column = "TYPE"),

        @Result(property = "orgLevel1", column = "ORG_LEVEL_1"),
        @Result(property = "orgLevel2", column = "ORG_LEVEL_2"),
        @Result(property = "orgLevel3", column = "ORG_LEVEL_3"),
        @Result(property = "orgLevel4", column = "ORG_LEVEL_4") })
    List<WorkbasketSummaryImpl> findByDistributionTargets(@Param("id") String id);

    @Select("SELECT ID, KEY, NAME, DESCRIPTION, OWNER, DOMAIN, TYPE, ORG_LEVEL_1, ORG_LEVEL_2, ORG_LEVEL_3, ORG_LEVEL_4  FROM WORKBASKET WHERE key = #{key}")
    @Results(value = {
        @Result(property = "id", column = "ID"),
        @Result(property = "key", column = "KEY"),
        @Result(property = "name", column = "NAME"),
        @Result(property = "description", column = "DESCRIPTION"),
        @Result(property = "owner", column = "OWNER"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "type", column = "TYPE"),
        @Result(property = "orgLevel1", column = "ORG_LEVEL_1"),
        @Result(property = "orgLevel2", column = "ORG_LEVEL_2"),
        @Result(property = "orgLevel3", column = "ORG_LEVEL_3"),
        @Result(property = "orgLevel4", column = "ORG_LEVEL_4")})

    List<WorkbasketSummaryImpl> findSummaryByKey(@Param("key") String key);

    @Select("SELECT * FROM WORKBASKET ORDER BY id")
    @Results(value = {
        @Result(property = "id", column = "ID"),
        @Result(property = "key", column = "KEY"),
        @Result(property = "name", column = "NAME"),
        @Result(property = "description", column = "DESCRIPTION"),
        @Result(property = "owner", column = "OWNER"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "type", column = "TYPE"),
        @Result(property = "orgLevel1", column = "ORG_LEVEL_1"),
        @Result(property = "orgLevel2", column = "ORG_LEVEL_2"),
        @Result(property = "orgLevel3", column = "ORG_LEVEL_3"),
        @Result(property = "orgLevel4", column = "ORG_LEVEL_4") })
    List<WorkbasketSummaryImpl> findAll();

    @Select("<script>SELECT W.ID, W.KEY, W.NAME, W.DESCRIPTION, W.OWNER, W.DOMAIN, W.TYPE, W.ORG_LEVEL_1, W.ORG_LEVEL_2,  W.ORG_LEVEL_3, W.ORG_LEVEL_4 FROM WORKBASKET AS W "
        + "INNER JOIN WORKBASKET_ACCESS_LIST AS ACL "
        + "ON (W.KEY = ACL.WORKBASKET_KEY AND ACL.ACCESS_ID = #{accessId}) "
        + "WHERE <foreach collection='authorizations' item='authorization' separator=' AND '>"
        + "<if test=\"authorization.name() == 'OPEN'\">PERM_OPEN</if>"
        + "<if test=\"authorization.name() == 'READ'\">PERM_READ</if>"
        + "<if test=\"authorization.name() == 'APPEND'\">PERM_APPEND</if>"
        + "<if test=\"authorization.name() == 'TRANSFER'\">PERM_TRANSFER</if>"
        + "<if test=\"authorization.name() == 'DISTRIBUTE'\">PERM_DISTRIBUTE</if>"
        + "<if test=\"authorization.name() == 'CUSTOM_1'\">PERM_CUSTOM_1</if>"
        + "<if test=\"authorization.name() == 'CUSTOM_2'\">PERM_CUSTOM_2</if>"
        + "<if test=\"authorization.name() == 'CUSTOM_3'\">PERM_CUSTOM_3</if>"
        + "<if test=\"authorization.name() == 'CUSTOM_4'\">PERM_CUSTOM_4</if>"
        + "<if test=\"authorization.name() == 'CUSTOM_5'\">PERM_CUSTOM_5</if>"
        + "<if test=\"authorization.name() == 'CUSTOM_6'\">PERM_CUSTOM_6</if>"
        + "<if test=\"authorization.name() == 'CUSTOM_7'\">PERM_CUSTOM_7</if>"
        + "<if test=\"authorization.name() == 'CUSTOM_8'\">PERM_CUSTOM_8</if> = 1 </foreach> "
        + "ORDER BY id</script>")
    @Results(value = {
        @Result(property = "id", column = "ID"),
        @Result(property = "key", column = "KEY"),
        @Result(property = "modified", column = "MODIFIED"),
        @Result(property = "name", column = "NAME"),
        @Result(property = "description", column = "DESCRIPTION"),
        @Result(property = "owner", column = "OWNER"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "type", column = "TYPE"),

        @Result(property = "orgLevel1", column = "ORG_LEVEL_1"),
        @Result(property = "orgLevel2", column = "ORG_LEVEL_2"),
        @Result(property = "orgLevel3", column = "ORG_LEVEL_3"),
        @Result(property = "orgLevel4", column = "ORG_LEVEL_4") })
    List<WorkbasketSummaryImpl> findByPermission(@Param("authorizations") List<WorkbasketAuthorization> authorizations,
        @Param("accessId") String accessId);

    @Insert("INSERT INTO WORKBASKET (ID, KEY, CREATED, MODIFIED, NAME, DOMAIN, TYPE, DESCRIPTION, OWNER, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, ORG_LEVEL_1, ORG_LEVEL_2, ORG_LEVEL_3, ORG_LEVEL_4) VALUES (#{workbasket.id}, #{workbasket.key}, #{workbasket.created}, #{workbasket.modified}, #{workbasket.name}, #{workbasket.domain}, #{workbasket.type}, #{workbasket.description}, #{workbasket.owner}, #{workbasket.custom1}, #{workbasket.custom2}, #{workbasket.custom3}, #{workbasket.custom4}, #{workbasket.orgLevel1}, #{workbasket.orgLevel2}, #{workbasket.orgLevel3}, #{workbasket.orgLevel4})")
    @Options(keyProperty = "id", keyColumn = "ID")
    void insert(@Param("workbasket") WorkbasketImpl workbasket);

    @Update("UPDATE WORKBASKET SET MODIFIED = #{workbasket.modified}, KEY = #{workbasket.key}, NAME = #{workbasket.name}, DOMAIN = #{workbasket.domain}, TYPE = #{workbasket.type}, DESCRIPTION = #{workbasket.description}, OWNER = #{workbasket.owner}, CUSTOM_1 = #{workbasket.custom1}, CUSTOM_2 = #{workbasket.custom2}, CUSTOM_3 = #{workbasket.custom3}, CUSTOM_4 = #{workbasket.custom4}, ORG_LEVEL_1 = #{workbasket.orgLevel1}, ORG_LEVEL_2 = #{workbasket.orgLevel2}, ORG_LEVEL_3 = #{workbasket.orgLevel3}, ORG_LEVEL_4 = #{workbasket.orgLevel4}  WHERE id = #{workbasket.id}")
    void update(@Param("workbasket") WorkbasketImpl workbasket);

    @Delete("DELETE FROM WORKBASKET where id = #{id}")
    void delete(@Param("id") String id);
}
