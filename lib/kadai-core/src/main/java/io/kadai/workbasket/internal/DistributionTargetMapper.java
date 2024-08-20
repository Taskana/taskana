package io.kadai.workbasket.internal;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/** This class is the mybatis mapping of distribution targets. */
@SuppressWarnings("checkstyle:LineLength")
public interface DistributionTargetMapper {

  @Insert(
      "INSERT INTO DISTRIBUTION_TARGETS (SOURCE_ID, TARGET_ID) VALUES (#{sourceId}, #{targetId})")
  void insert(@Param("sourceId") String sourceId, @Param("targetId") String targetId);

  @Delete(
      "DELETE FROM DISTRIBUTION_TARGETS WHERE SOURCE_ID = #{sourceId} AND TARGET_ID = #{targetId}")
  void delete(@Param("sourceId") String sourceId, @Param("targetId") String targetId);

  @Select(
      "<script>SELECT TARGET_ID FROM DISTRIBUTION_TARGETS WHERE SOURCE_ID = #{sourceId} "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  List<String> findBySourceId(@Param("sourceId") String sourceId);

  @Select(
      "<script>SELECT count(*) FROM DISTRIBUTION_TARGETS WHERE SOURCE_ID = #{sourceId} AND TARGET_ID = #{targetId}"
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  int getNumberOfDistributionTargets(
      @Param("sourceId") String sourceId, @Param("targetId") String targetId);

  @Delete(
      "<script>DELETE FROM DISTRIBUTION_TARGETS WHERE SOURCE_ID = #{sourceId} AND TARGET_ID IN (<foreach item='target' collection='targetId' separator=',' > #{target} </foreach>)</script>")
  void deleteMultipleBySourceId(
      @Param("sourceId") String sourceId, @Param("targetId") List<String> targetId);

  @Delete("DELETE FROM DISTRIBUTION_TARGETS WHERE SOURCE_ID = #{sourceId}")
  void deleteAllDistributionTargetsBySourceId(@Param("sourceId") String sourceId);

  @Delete("DELETE FROM DISTRIBUTION_TARGETS WHERE TARGET_ID = #{targetId}")
  void deleteAllDistributionTargetsByTargetId(@Param("targetId") String targetId);
}
