package pro.taskana.impl;

import java.util.ArrayList;
import java.util.List;

import pro.taskana.ClassificationQuery;
import pro.taskana.ClassificationSummary;
import pro.taskana.TimeInterval;

/**
 * Created by BV on 26.10.2017.
 */
public class TestClassificationQuery implements ClassificationQuery {

    private List<ClassificationSummaryImpl> classifications;
    private String[] parentId;
    private String[] parentKey;
    private String description;

    public TestClassificationQuery(List<ClassificationSummaryImpl> classifications) {
        this.classifications = classifications;
    }

    @Override
    public ClassificationQuery keyIn(String... key) {
        return this;
    }

    @Override
    public ClassificationQuery idIn(String... id) {
        return this;
    }

    @Override
    public ClassificationQuery parentIdIn(String... parentId) {
        this.parentId = parentId;
        return this;
    }

    @Override
    public ClassificationQuery parentKeyIn(String... parentKey) {
        this.parentKey = parentKey;
        return this;
    }

    @Override
    public ClassificationQuery categoryIn(String... category) {
        return this;
    }

    @Override
    public ClassificationQuery typeIn(String... type) {
        return this;
    }

    @Override
    public ClassificationQuery domainIn(String... domain) {
        return this;
    }

    @Override
    public ClassificationQuery validInDomainEquals(Boolean validInDomain) {
        return this;
    }

    @Override
    public ClassificationQuery createdWithin(TimeInterval... created) {
        return this;
    }

    @Override
    public ClassificationQuery nameIn(String... name) {
        return this;
    }

    @Override
    public ClassificationQuery nameLike(String... nameLike) {
        return this;
    }

    @Override
    public ClassificationQuery descriptionLike(String description) {
        return this;
    }

    @Override
    public ClassificationQuery priorityIn(int... priorities) {
        return this;
    }

    @Override
    public ClassificationQuery serviceLevelIn(String... serviceLevel) {
        return this;
    }

    @Override
    public ClassificationQuery serviceLevelLike(String... serviceLevelLike) {
        return this;
    }

    @Override
    public ClassificationQuery applicationEntryPointIn(String... applicationEntryPoint) {
        return null;
    }

    @Override
    public ClassificationQuery applicationEntryPointLike(String... applicationEntryPointLike) {
        return this;
    }

    @Override
    public ClassificationQuery customAttributeIn(String num, String... customFields) {
        return this;
    }

    @Override
    public ClassificationQuery customAttributeLike(String num, String... custom1Like) {
        return this;
    }

    @Override
    public List<ClassificationSummary> list() {
        List<ClassificationSummary> returnedClassifications = new ArrayList<>();
        returnedClassifications.addAll(classifications);
        return returnedClassifications;
    }

    @Override
    public List<ClassificationSummary> list(int offset, int limit) {
        return new ArrayList<>();
    }

    @Override
    public ClassificationSummary single() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public ClassificationQuery orderByKey(SortDirection sortDirection) {
        return this;
    }

    @Override
    public ClassificationQuery orderByParentId(SortDirection sortDirection) {
        return this;
    }

    @Override
    public ClassificationQuery orderByParentKey(SortDirection sortDirection) {
        return this;
    }

    @Override
    public ClassificationQuery orderByCategory(SortDirection sortDirection) {
        return this;
    }

    @Override
    public ClassificationQuery orderByDomain(SortDirection sortDirection) {
        return this;
    }

    @Override
    public ClassificationQuery orderByName(SortDirection sortDirection) {
        return this;
    }

    @Override
    public ClassificationQuery orderByServiceLevel(SortDirection sortDirection) {
        return this;
    }

    @Override
    public ClassificationQuery orderByPriority(SortDirection sortDirection) {
        return this;
    }

    @Override
    public ClassificationQuery orderByApplicationEntryPoint(SortDirection sortDirection) {
        return this;
    }

    @Override
    public ClassificationQuery orderByCustomAttribute(String num, SortDirection sortDirection) {
        return this;
    }

    @Override
    public ClassificationQuery modifiedWithin(TimeInterval... modifiedIn) {
        return this;
    }

    @Override
    public List<String> listValues(String dbColumnName, SortDirection sortDirection) {
        return new ArrayList<>();
    }

}
