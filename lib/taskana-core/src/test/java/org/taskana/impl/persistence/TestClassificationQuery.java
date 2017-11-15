package org.taskana.impl.persistence;

import org.taskana.exceptions.NotAuthorizedException;
import org.taskana.model.Classification;
import org.taskana.persistence.ClassificationQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by BV on 26.10.2017.
 */
public class TestClassificationQuery implements ClassificationQuery {

    private List<Classification> classifications;
    private String[] parentId;
    private Date[] validUntil;
    private String description;

    public TestClassificationQuery(List<Classification> classifications) {
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
        for (Classification classification : classifications) {
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
                if (classification.getParentClassificationId() != null) {
                    for (String parent : this.parentId) {
                        if (parent.equals(classification.getParentClassificationId())) {
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
    public Classification single() throws NotAuthorizedException {
        return null;
    }
}
