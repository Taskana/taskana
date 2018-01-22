package pro.taskana.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import pro.taskana.ClassificationQuery;
import pro.taskana.ClassificationSummary;
import pro.taskana.exceptions.NotAuthorizedException;

/**
 * Created by BV on 26.10.2017.
 */
public class TestClassificationQuery implements ClassificationQuery {

    private List<ClassificationSummaryImpl> classifications;
    private String[] parentClassificationKey;
    private String description;

    public TestClassificationQuery(List<ClassificationSummaryImpl> classifications) {
        this.classifications = classifications;
    }

    @Override
    public ClassificationQuery key(String... key) {
        return this;
    }

    @Override
    public ClassificationQuery parentClassificationKey(String... parentClassificationKey) {
        this.parentClassificationKey = parentClassificationKey;
        return this;
    }

    @Override
    public ClassificationQuery category(String... category) {
        return this;
    }

    @Override
    public ClassificationQuery type(String... type) {
        return this;
    }

    @Override
    public ClassificationQuery domain(String... domain) {
        return this;
    }

    @Override
    public ClassificationQuery validInDomain(Boolean validInDomain) {
        return this;
    }

    @Override
    public ClassificationQuery created(Instant... created) {
        return this;
    }

    @Override
    public ClassificationQuery name(String... name) {
        return this;
    }

    @Override
    public ClassificationQuery descriptionLike(String description) {
        return this;
    }

    @Override
    public ClassificationQuery priority(int... priorities) {
        return this;
    }

    @Override
    public ClassificationQuery serviceLevel(String... serviceLevel) {
        return this;
    }

    @Override
    public ClassificationQuery applicationEntryPoint(String... applicationEntryPoint) {
        return null;
    }

    @Override
    public ClassificationQuery customFields(String... customFields) {
        return this;
    }

    @Override
    public List<ClassificationSummary> list() throws NotAuthorizedException {
        List<ClassificationSummary> returnedClassifications = new ArrayList<>();
        returnedClassifications.addAll(classifications);
        return returnedClassifications;
    }

    @Override
    public List<ClassificationSummary> list(int offset, int limit) throws NotAuthorizedException {
        return null;
    }

    @Override
    public ClassificationSummary single() throws NotAuthorizedException {
        return null;
    }
}
