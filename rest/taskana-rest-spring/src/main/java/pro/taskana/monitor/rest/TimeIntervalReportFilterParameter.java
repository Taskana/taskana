package pro.taskana.monitor.rest;

import static pro.taskana.common.internal.util.CheckedConsumer.wrap;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.common.rest.QueryParameter;
import pro.taskana.monitor.api.reports.TimeIntervalReportBuilder;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskState;

public class TimeIntervalReportFilterParameter extends ReportFilterParameter
    implements QueryParameter<TimeIntervalReportBuilder<?, ?, TimeIntervalColumnHeader>, Void> {

  @ConstructorProperties({
    "in-working-days",
    "workbasket-id",
    "state",
    "classification-category",
    "domain",
    "classification-id",
    "excluded-classification-id",
    "custom-1",
    "custom-1-like",
    "custom-1-not-in",
    "custom-2",
    "custom-2-like",
    "custom-2-not-in",
    "custom-3",
    "custom-3-like",
    "custom-3-not-in",
    "custom-4",
    "custom-4-like",
    "custom-4-not-in",
    "custom-5",
    "custom-5-like",
    "custom-5-not-in",
    "custom-6",
    "custom-6-like",
    "custom-6-not-in",
    "custom-7",
    "custom-7-like",
    "custom-7-not-in",
    "custom-8",
    "custom-8-like",
    "custom-8-not-in",
    "custom-9",
    "custom-9-like",
    "custom-9-not-in",
    "custom-10",
    "custom-10-like",
    "custom-10-not-in",
    "custom-11",
    "custom-11-like",
    "custom-11-not-in",
    "custom-12",
    "custom-12-like",
    "custom-12-not-in",
    "custom-13",
    "custom-13-like",
    "custom-13-not-in",
    "custom-14",
    "custom-14-like",
    "custom-14-not-in",
    "custom-15",
    "custom-15-like",
    "custom-15-not-in",
    "custom-16",
    "custom-16-like",
    "custom-16-not-in"
  })
  public TimeIntervalReportFilterParameter(
      Boolean inWorkingDays,
      String[] workbasketId,
      TaskState[] state,
      String[] classificationCategory,
      String[] domain,
      String[] classificationId,
      String[] excludedClassificationId,
      String[] custom1,
      String[] custom1Like,
      String[] custom1NotIn,
      String[] custom2,
      String[] custom2Like,
      String[] custom2NotIn,
      String[] custom3,
      String[] custom3Like,
      String[] custom3NotIn,
      String[] custom4,
      String[] custom4Like,
      String[] custom4NotIn,
      String[] custom5,
      String[] custom5Like,
      String[] custom5NotIn,
      String[] custom6,
      String[] custom6Like,
      String[] custom6NotIn,
      String[] custom7,
      String[] custom7Like,
      String[] custom7NotIn,
      String[] custom8,
      String[] custom8Like,
      String[] custom8NotIn,
      String[] custom9,
      String[] custom9Like,
      String[] custom9NotIn,
      String[] custom10,
      String[] custom10Like,
      String[] custom10NotIn,
      String[] custom11,
      String[] custom11Like,
      String[] custom11NotIn,
      String[] custom12,
      String[] custom12Like,
      String[] custom12NotIn,
      String[] custom13,
      String[] custom13Like,
      String[] custom13NotIn,
      String[] custom14,
      String[] custom14Like,
      String[] custom14NotIn,
      String[] custom15,
      String[] custom15Like,
      String[] custom15NotIn,
      String[] custom16,
      String[] custom16Like,
      String[] custom16NotIn) {
    super(
        inWorkingDays,
        workbasketId,
        state,
        classificationCategory,
        domain,
        classificationId,
        excludedClassificationId,
        custom1,
        custom1Like,
        custom1NotIn,
        custom2,
        custom2Like,
        custom2NotIn,
        custom3,
        custom3Like,
        custom3NotIn,
        custom4,
        custom4Like,
        custom4NotIn,
        custom5,
        custom5Like,
        custom5NotIn,
        custom6,
        custom6Like,
        custom6NotIn,
        custom7,
        custom7Like,
        custom7NotIn,
        custom8,
        custom8Like,
        custom8NotIn,
        custom9,
        custom9Like,
        custom9NotIn,
        custom10,
        custom10Like,
        custom10NotIn,
        custom11,
        custom11Like,
        custom11NotIn,
        custom12,
        custom12Like,
        custom12NotIn,
        custom13,
        custom13Like,
        custom13NotIn,
        custom14,
        custom14Like,
        custom14NotIn,
        custom15,
        custom15Like,
        custom15NotIn,
        custom16,
        custom16Like,
        custom16NotIn);
  }

  @Override
  public Void apply(TimeIntervalReportBuilder<?, ?, TimeIntervalColumnHeader> builder) {
    builder.withColumnHeaders(defaultColumnHeaders());
    Optional.ofNullable(inWorkingDays)
        .ifPresent(
            bool -> {
              if (Boolean.TRUE.equals(bool)) {
                builder.inWorkingDays();
              }
            });
    Optional.ofNullable(workbasketId).map(Arrays::asList).ifPresent(builder::workbasketIdIn);
    Optional.ofNullable(state).map(Arrays::asList).ifPresent(builder::stateIn);
    Optional.ofNullable(classificationCategory)
        .map(Arrays::asList)
        .ifPresent(builder::classificationCategoryIn);
    Optional.ofNullable(domain).map(Arrays::asList).ifPresent(builder::domainIn);
    Optional.ofNullable(classificationId)
        .map(Arrays::asList)
        .ifPresent(builder::classificationIdIn);
    Optional.ofNullable(excludedClassificationId)
        .map(Arrays::asList)
        .ifPresent(builder::excludedClassificationIdIn);

    Stream.of(
            Pair.of(TaskCustomField.CUSTOM_1, custom1),
            Pair.of(TaskCustomField.CUSTOM_2, custom2),
            Pair.of(TaskCustomField.CUSTOM_3, custom3),
            Pair.of(TaskCustomField.CUSTOM_4, custom4),
            Pair.of(TaskCustomField.CUSTOM_5, custom5),
            Pair.of(TaskCustomField.CUSTOM_6, custom6),
            Pair.of(TaskCustomField.CUSTOM_7, custom7),
            Pair.of(TaskCustomField.CUSTOM_8, custom8),
            Pair.of(TaskCustomField.CUSTOM_9, custom9),
            Pair.of(TaskCustomField.CUSTOM_10, custom10),
            Pair.of(TaskCustomField.CUSTOM_11, custom11),
            Pair.of(TaskCustomField.CUSTOM_12, custom12),
            Pair.of(TaskCustomField.CUSTOM_13, custom13),
            Pair.of(TaskCustomField.CUSTOM_14, custom14),
            Pair.of(TaskCustomField.CUSTOM_15, custom15),
            Pair.of(TaskCustomField.CUSTOM_16, custom16))
        .forEach(
            pair ->
                Optional.ofNullable(pair.getRight())
                    .ifPresent(wrap(l -> builder.customAttributeIn(pair.getLeft(), l))));
    Stream.of(
            Pair.of(TaskCustomField.CUSTOM_1, custom1Like),
            Pair.of(TaskCustomField.CUSTOM_2, custom2Like),
            Pair.of(TaskCustomField.CUSTOM_3, custom3Like),
            Pair.of(TaskCustomField.CUSTOM_4, custom4Like),
            Pair.of(TaskCustomField.CUSTOM_5, custom5Like),
            Pair.of(TaskCustomField.CUSTOM_6, custom6Like),
            Pair.of(TaskCustomField.CUSTOM_7, custom7Like),
            Pair.of(TaskCustomField.CUSTOM_8, custom8Like),
            Pair.of(TaskCustomField.CUSTOM_9, custom9Like),
            Pair.of(TaskCustomField.CUSTOM_10, custom10Like),
            Pair.of(TaskCustomField.CUSTOM_11, custom11Like),
            Pair.of(TaskCustomField.CUSTOM_12, custom12Like),
            Pair.of(TaskCustomField.CUSTOM_13, custom13Like),
            Pair.of(TaskCustomField.CUSTOM_14, custom14Like),
            Pair.of(TaskCustomField.CUSTOM_15, custom15Like),
            Pair.of(TaskCustomField.CUSTOM_16, custom16Like))
        .forEach(
            pair ->
                Optional.ofNullable(pair.getRight())
                    .map(this::wrapElementsInLikeStatement)
                    .ifPresent(wrap(l -> builder.customAttributeLike(pair.getLeft(), l))));

    Stream.of(
            Pair.of(TaskCustomField.CUSTOM_1, custom1NotIn),
            Pair.of(TaskCustomField.CUSTOM_2, custom2NotIn),
            Pair.of(TaskCustomField.CUSTOM_3, custom3NotIn),
            Pair.of(TaskCustomField.CUSTOM_4, custom4NotIn),
            Pair.of(TaskCustomField.CUSTOM_5, custom5NotIn),
            Pair.of(TaskCustomField.CUSTOM_6, custom6NotIn),
            Pair.of(TaskCustomField.CUSTOM_7, custom7NotIn),
            Pair.of(TaskCustomField.CUSTOM_8, custom8NotIn),
            Pair.of(TaskCustomField.CUSTOM_9, custom9NotIn),
            Pair.of(TaskCustomField.CUSTOM_10, custom10NotIn),
            Pair.of(TaskCustomField.CUSTOM_11, custom11NotIn),
            Pair.of(TaskCustomField.CUSTOM_12, custom12NotIn),
            Pair.of(TaskCustomField.CUSTOM_13, custom13NotIn),
            Pair.of(TaskCustomField.CUSTOM_14, custom14NotIn),
            Pair.of(TaskCustomField.CUSTOM_15, custom15NotIn),
            Pair.of(TaskCustomField.CUSTOM_16, custom16NotIn))
        .forEach(
            pair ->
                Optional.ofNullable(pair.getRight())
                    .ifPresent(wrap(l -> builder.customAttributeNotIn(pair.getLeft(), l))));
    return null;
  }

  private List<TimeIntervalColumnHeader> defaultColumnHeaders() {
    return Stream.concat(
            Stream.of(
                new TimeIntervalColumnHeader.Range(Integer.MIN_VALUE, -11),
                new TimeIntervalColumnHeader.Range(-10, -5),
                new TimeIntervalColumnHeader.Range(5, 10),
                new TimeIntervalColumnHeader.Range(11, Integer.MAX_VALUE)),
            Stream.of(-4, -3, -2, -1, 0, 1, 2, 3, 4).map(TimeIntervalColumnHeader.Range::new))
        .sorted(Comparator.comparing(TimeIntervalColumnHeader::getLowerAgeLimit))
        .collect(Collectors.toList());
  }
}
