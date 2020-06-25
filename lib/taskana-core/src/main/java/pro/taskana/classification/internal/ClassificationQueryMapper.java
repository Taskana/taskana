package pro.taskana.classification.internal;

import java.util.List;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;

import pro.taskana.classification.internal.models.ClassificationSummaryImpl;
import pro.taskana.classification.internal.util.ClassificationQueryMapperQueryProvider;

/** This class provides a mapper for all classification queries. */
@SuppressWarnings({"checkstyle:LineLength", "checkstyle:Indentation"})
public interface ClassificationQueryMapper {
  @SelectProvider(
      type = ClassificationQueryMapperQueryProvider.class,
      method = "getClassificationSummariesQuery")
  @Results({
    @Result(property = "id", column = "ID"),
    @Result(property = "key", column = "KEY"),
    @Result(property = "category", column = "CATEGORY"),
    @Result(property = "type", column = "TYPE"),
    @Result(property = "domain", column = "DOMAIN"),
    @Result(property = "name", column = "NAME"),
    @Result(property = "priority", column = "PRIORITY"),
    @Result(property = "serviceLevel", column = "SERVICE_LEVEL"),
    @Result(property = "parentId", column = "PARENT_ID"),
    @Result(property = "parentKey", column = "PARENT_KEY"),
    @Result(property = "applicationEntryPoint", column = "APPLICATION_ENTRY_POINT"),
    @Result(property = "custom1", column = "CUSTOM_1"),
    @Result(property = "custom2", column = "CUSTOM_2"),
    @Result(property = "custom3", column = "CUSTOM_3"),
    @Result(property = "custom4", column = "CUSTOM_4"),
    @Result(property = "custom5", column = "CUSTOM_5"),
    @Result(property = "custom6", column = "CUSTOM_6"),
    @Result(property = "custom7", column = "CUSTOM_7"),
    @Result(property = "custom8", column = "CUSTOM_8")
  })
  List<ClassificationSummaryImpl> queryClassificationSummaries(
      ClassificationQueryImpl classificationQuery);

  @SelectProvider(
      type = ClassificationQueryMapperQueryProvider.class,
      method = "getCountClassificationsQuery")
  Long countQueryClassifications(ClassificationQueryImpl classificationQuery);

  @SelectProvider(
      type = ClassificationQueryMapperQueryProvider.class,
      method = "getClassificationColumnValuesQuery")
  List<String> queryClassificationColumnValues(ClassificationQueryImpl classificationQuery);
}
