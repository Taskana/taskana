package pro.taskana.impl;

import pro.taskana.TaskanaEngine;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketAccessItemExtended;
import pro.taskana.WorkbasketAccessItemQuery;

/**
 * WorkbasketAccessItemQueryImpl for generating dynamic SQL.
 */
public class WorkbasketAccessItemQueryImpl
    extends AbstractWorkbasketAccessItemQueryImpl<WorkbasketAccessItemQuery, WorkbasketAccessItem>
    implements WorkbasketAccessItemQuery {

    private static final String LINK_TO_MAPPER = "pro.taskana.mappings.QueryMapper.queryWorkbasketAccessItems";
    private static final String LINK_TO_VALUE_MAPPER = "pro.taskana.mappings.QueryMapper.queryWorkbasketAccessItemColumnValues";

    WorkbasketAccessItemQueryImpl(TaskanaEngine taskanaEngine) {
        super(taskanaEngine);
    }

    @Override
    WorkbasketAccessItemQuery _this() {
        return this;
    }

    @Override
    String getLinkToMapper() {
        return LINK_TO_MAPPER;
    }

    @Override
    String getLinkToValueMapper() {
        return LINK_TO_VALUE_MAPPER;
    }

    /**
     * Extended version of {@link WorkbasketAccessItemQueryImpl}.
     */
    public static class Extended
        extends AbstractWorkbasketAccessItemQueryImpl<WorkbasketAccessItemQuery.Extended, WorkbasketAccessItemExtended>
        implements WorkbasketAccessItemQuery.Extended {

        private static final String LINK_TO_MAPPER_EXTENDED = "pro.taskana.mappings.QueryMapper.queryWorkbasketAccessItemsExtended";
        private static final String LINK_TO_VALUE_MAPPER_EXTENDED = "pro.taskana.mappings.QueryMapper.queryWorkbasketAccessItemExtendedColumnValues";

        private String[] workbasketKeyIn;
        private String[] workbasketKeyLike;
        private String[] accessIdLike;

        Extended(TaskanaEngine taskanaEngine) {
            super(taskanaEngine);
        }

        @Override
        WorkbasketAccessItemQuery.Extended _this() {
            return this;
        }

        @Override
        String getLinkToMapper() {
            return LINK_TO_MAPPER_EXTENDED;
        }

        @Override
        String getLinkToValueMapper() {
            return LINK_TO_VALUE_MAPPER_EXTENDED;
        }

        @Override
        public WorkbasketAccessItemQuery.Extended workbasketKeyIn(String... keys) {
            this.workbasketKeyIn = keys;
            return this;
        }

        @Override
        public WorkbasketAccessItemQuery.Extended orderByWorkbasketKey(SortDirection sortDirection) {
            return addOrderCriteria("WB.KEY", sortDirection);
        }

        @Override
        public WorkbasketAccessItemQuery.Extended workbasketKeyLike(String... key) {
            this.workbasketKeyLike = toUpperCopy(key);
            return this;
        }

        @Override
        public WorkbasketAccessItemQuery.Extended accessIdLike(String... ids) {
            this.accessIdLike = toUpperCopy(ids);
            return this;
        }

        public String[] getWorkbasketKeyIn() {
            return workbasketKeyIn;
        }

        public String[] getAccessIdLike() {
            return accessIdLike;
        }

        public String[] getWorkbasketKeyLike() {
            return workbasketKeyLike;
        }

    }

}

