package pro.taskana.model.mappings;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
/**
 * This class is the mybatis mapping of distribution targets.
 */
public interface DistributionTargetMapper {

    @Insert("INSERT INTO DISTRIBUTION_TARGETS (SOURCE_ID, TARGET_ID) VALUES (#{sourceId}, #{targetId})")
    void insert(@Param("sourceId") String sourceId, @Param("targetId") String targetId);

    @Delete("DELETE FROM DISTRIBUTION_TARGETS WHERE SOURCE_ID = #{sourceId} AND TARGET_ID = #{targetId}")
    void delete(@Param("sourceId") String sourceId, @Param("targetId") String targetId);

    @Select("SELECT TARGET_ID FROM DISTRIBUTION_TARGETS WHERE SOURCE_ID = #{sourceId}")
    List<String> findBySourceId(@Param("sourceId") String sourceId);

    @Delete("<script>DELETE FROM DISTRIBUTION_TARGETS WHERE SOURCE_ID = #{sourceId} AND TARGET_ID IN (<foreach item='target' collection='targetId' separator=',' > #{target} </foreach>)</script>")
    void deleteMultiple(@Param("sourceId") String sourceId, @Param("targetId") List<String> targetId);
}
