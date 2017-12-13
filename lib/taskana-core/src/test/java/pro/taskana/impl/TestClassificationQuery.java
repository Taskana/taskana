package pro.taskana.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pro.taskana.Classification;
import pro.taskana.ClassificationQuery;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.model.ClassificationImpl;

/**
 * Created by BV on 26.10.2017.
 */
public class TestClassificationQuery implements ClassificationQuery {

    private List<ClassificationImpl> classifications;

    private String[] parentId;

    private Date[] validUntil;

    private String description;

    public TestClassificationQuery(List<ClassificationImpl> classifications) {
        this.classifications = classifications;

    }

    @Override
    public ClassificationQuery parentClassification(String... parentClassificationId) {
        parentId = parentClassificationId;
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
    public ClassificationQuery created(Date... created) {
        return this;
    }

    @Override
    public ClassificationQuery name(String... name) {
        return this;
    }

    @Override
    public ClassificationQuery descriptionLike(String description) {
        this.description = description;
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
    public ClassificationQuery customFields(String... customFields) {
        return this;
    }

    @Override
    public ClassificationQuery validFrom(Date... validFrom) {
        return this;
    }

    @Override
    public ClassificationQuery validUntil(Date... validUntil) {
        this.validUntil = validUntil;
        return this;
    }

    @Override
    public List<Classification> list() throws NotAuthorizedException {
        List<Classification> returnedClassifications = new ArrayList<>();
        returnedClassifications.addAll(classifications);
        for (ClassificationImpl classification : classifications) {
            if (this.validUntil != null) {
                boolean validDate = false;
                for (Date valid : validUntil) {
                    if (!classification.getValidUntil().before(valid)) {
                        validDate = true;
                    }
                }
                if (!validDate) {
                    returnedClassifications.remove(classification);
                }
            }

            if (this.parentId != null) {
                Boolean classificationWithParent = false;
                if (classification.getParentClassificationKey() != null) {
                    for (String parent : this.parentId) {
                        if (parent.equals(classification.getParentClassificationKey())) {
                            classificationWithParent = true;
                        }
                    }
                }
                if (!classificationWithParent) {
                    returnedClassifications.remove(classification);
                }
            }

            if (this.description != null) {
                if (classification.getDescription() != description) {
                    returnedClassifications.remove(classification);
                }
            }
        }
        return returnedClassifications;

    }

    @Override
    public List<Classification> list(int offset, int limit) throws NotAuthorizedException {
        return null;
    }

    @Override
    public ClassificationImpl single() throws NotAuthorizedException {
        return null;
    }
}
