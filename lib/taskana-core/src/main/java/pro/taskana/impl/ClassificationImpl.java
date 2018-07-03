package pro.taskana.impl;

import java.time.Instant;

import pro.taskana.Classification;
import pro.taskana.ClassificationSummary;

/**
 * Classification entity.
 */
public class ClassificationImpl implements Classification {

    private String id;
    private String key;
    private String parentId;
    private String parentKey;
    private String category;
    private String type;
    private String domain;
    private Boolean isValidInDomain;
    private Instant created;
    private Instant modified;
    private String name;
    private String description;
    private int priority;
    private String serviceLevel; // PddDThhHmmM
    private String applicationEntryPoint;
    private String custom1;
    private String custom2;
    private String custom3;
    private String custom4;
    private String custom5;
    private String custom6;
    private String custom7;
    private String custom8;

    ClassificationImpl() {
    }

    ClassificationImpl(ClassificationImpl classification) {
        this.id = classification.getId();
        this.key = classification.getKey();
        this.parentId = classification.getParentId();
        this.parentKey = classification.getParentKey();
        this.category = classification.getCategory();
        this.type = classification.getType();
        this.domain = classification.getDomain();
        this.isValidInDomain = classification.getIsValidInDomain();
        this.created = classification.getCreated();
        this.modified = classification.getModified();
        this.name = classification.getName();
        this.description = classification.getDescription();
        this.priority = classification.getPriority();
        this.serviceLevel = classification.getServiceLevel();
        this.applicationEntryPoint = classification.getApplicationEntryPoint();
        this.custom1 = classification.getCustom1();
        this.custom2 = classification.getCustom2();
        this.custom3 = classification.getCustom3();
        this.custom4 = classification.getCustom4();
        this.custom5 = classification.getCustom5();
        this.custom6 = classification.getCustom6();
        this.custom7 = classification.getCustom7();
        this.custom8 = classification.getCustom8();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public String getParentKey() {
        return parentKey;
    }

    @Override
    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public Boolean getIsValidInDomain() {
        return isValidInDomain;
    }

    @Override
    public void setIsValidInDomain(Boolean isValidInDomain) {
        this.isValidInDomain = isValidInDomain;
    }

    @Override
    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    @Override
    public Instant getModified() {
        return modified;
    }

    public void setModified(Instant modified) {
        this.modified = modified;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String getServiceLevel() {
        return serviceLevel;
    }

    @Override
    public void setServiceLevel(String serviceLevel) {
        this.serviceLevel = serviceLevel;
    }

    @Override
    public String getApplicationEntryPoint() {
        return applicationEntryPoint;
    }

    @Override
    public void setApplicationEntryPoint(String applicationEntryPoint) {
        this.applicationEntryPoint = applicationEntryPoint;
    }

    @Override
    public String getCustom1() {
        return custom1;
    }

    @Override
    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }

    @Override
    public String getCustom2() {
        return custom2;
    }

    @Override
    public void setCustom2(String custom2) {
        this.custom2 = custom2;
    }

    @Override
    public String getCustom3() {
        return custom3;
    }

    @Override
    public void setCustom3(String custom3) {
        this.custom3 = custom3;
    }

    @Override
    public String getCustom4() {
        return custom4;
    }

    @Override
    public void setCustom4(String custom4) {
        this.custom4 = custom4;
    }

    @Override
    public String getCustom5() {
        return custom5;
    }

    @Override
    public void setCustom5(String custom5) {
        this.custom5 = custom5;
    }

    @Override
    public String getCustom6() {
        return custom6;
    }

    @Override
    public void setCustom6(String custom6) {
        this.custom6 = custom6;
    }

    @Override
    public String getCustom7() {
        return custom7;
    }

    @Override
    public void setCustom7(String custom7) {
        this.custom7 = custom7;
    }

    @Override
    public String getCustom8() {
        return custom8;
    }

    @Override
    public void setCustom8(String custom8) {
        this.custom8 = custom8;
    }

    @Override
    public ClassificationSummary asSummary() {
        ClassificationSummaryImpl summary = new ClassificationSummaryImpl();
        summary.setCategory(this.category);
        summary.setDomain(this.domain);
        summary.setId(this.id);
        summary.setKey(this.key);
        summary.setName(this.name);
        summary.setType(this.type);
        summary.setParentId(this.parentId);
        summary.setParentKey(this.parentKey);
        summary.setPriority(this.priority);
        summary.setServiceLevel(this.serviceLevel);
        summary.setCustom1(custom1);
        summary.setCustom2(custom2);
        summary.setCustom3(custom3);
        summary.setCustom4(custom4);
        summary.setCustom5(custom5);
        summary.setCustom6(custom6);
        summary.setCustom7(custom7);
        summary.setCustom8(custom8);
        return summary;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Classification [id=");
        builder.append(id);
        builder.append(", key=");
        builder.append(key);
        builder.append(", parentId=");
        builder.append(parentId);
        builder.append(", parentKey=");
        builder.append(parentKey);
        builder.append(", category=");
        builder.append(category);
        builder.append(", type=");
        builder.append(type);
        builder.append(", domain=");
        builder.append(domain);
        builder.append(", isValidInDomain=");
        builder.append(isValidInDomain);
        builder.append(", created=");
        builder.append(created);
        builder.append(", modified=");
        builder.append(modified);
        builder.append(", name=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", priority=");
        builder.append(priority);
        builder.append(", serviceLevel=");
        builder.append(serviceLevel);
        builder.append(", applicationEntryPoint=");
        builder.append(applicationEntryPoint);
        builder.append(", custom1=");
        builder.append(custom1);
        builder.append(", custom2=");
        builder.append(custom2);
        builder.append(", custom3=");
        builder.append(custom3);
        builder.append(", custom4=");
        builder.append(custom4);
        builder.append(", custom5=");
        builder.append(custom5);
        builder.append(", custom6=");
        builder.append(custom6);
        builder.append(", custom7=");
        builder.append(custom7);
        builder.append(", custom8=");
        builder.append(custom8);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((applicationEntryPoint == null) ? 0 : applicationEntryPoint.hashCode());
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((custom1 == null) ? 0 : custom1.hashCode());
        result = prime * result + ((custom2 == null) ? 0 : custom2.hashCode());
        result = prime * result + ((custom3 == null) ? 0 : custom3.hashCode());
        result = prime * result + ((custom4 == null) ? 0 : custom4.hashCode());
        result = prime * result + ((custom5 == null) ? 0 : custom5.hashCode());
        result = prime * result + ((custom6 == null) ? 0 : custom6.hashCode());
        result = prime * result + ((custom7 == null) ? 0 : custom7.hashCode());
        result = prime * result + ((custom8 == null) ? 0 : custom8.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((domain == null) ? 0 : domain.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((isValidInDomain == null) ? 0 : isValidInDomain.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((modified == null) ? 0 : modified.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
        result = prime * result + ((parentKey == null) ? 0 : parentKey.hashCode());
        result = prime * result + priority;
        result = prime * result + ((serviceLevel == null) ? 0 : serviceLevel.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ClassificationImpl other = (ClassificationImpl) obj;
        if (applicationEntryPoint == null) {
            if (other.applicationEntryPoint != null) {
                return false;
            }
        } else if (!applicationEntryPoint.equals(other.applicationEntryPoint)) {
            return false;
        }
        if (category == null) {
            if (other.category != null) {
                return false;
            }
        } else if (!category.equals(other.category)) {
            return false;
        }
        if (created == null) {
            if (other.created != null) {
                return false;
            }
        } else if (!created.equals(other.created)) {
            return false;
        }
        if (custom1 == null) {
            if (other.custom1 != null) {
                return false;
            }
        } else if (!custom1.equals(other.custom1)) {
            return false;
        }
        if (custom2 == null) {
            if (other.custom2 != null) {
                return false;
            }
        } else if (!custom2.equals(other.custom2)) {
            return false;
        }
        if (custom3 == null) {
            if (other.custom3 != null) {
                return false;
            }
        } else if (!custom3.equals(other.custom3)) {
            return false;
        }
        if (custom4 == null) {
            if (other.custom4 != null) {
                return false;
            }
        } else if (!custom4.equals(other.custom4)) {
            return false;
        }
        if (custom5 == null) {
            if (other.custom5 != null) {
                return false;
            }
        } else if (!custom5.equals(other.custom5)) {
            return false;
        }
        if (custom6 == null) {
            if (other.custom6 != null) {
                return false;
            }
        } else if (!custom6.equals(other.custom6)) {
            return false;
        }
        if (custom7 == null) {
            if (other.custom7 != null) {
                return false;
            }
        } else if (!custom7.equals(other.custom7)) {
            return false;
        }
        if (custom8 == null) {
            if (other.custom8 != null) {
                return false;
            }
        } else if (!custom8.equals(other.custom8)) {
            return false;
        }
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (domain == null) {
            if (other.domain != null) {
                return false;
            }
        } else if (!domain.equals(other.domain)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (isValidInDomain == null) {
            if (other.isValidInDomain != null) {
                return false;
            }
        } else if (!isValidInDomain.equals(other.isValidInDomain)) {
            return false;
        }
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        if (modified == null) {
            if (other.modified != null) {
                return false;
            }
        } else if (!modified.equals(other.modified)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (parentId == null) {
            if (other.parentId != null) {
                return false;
            }
        } else if (!parentId.equals(other.parentId)) {
            return false;
        }
        if (parentKey == null) {
            if (other.parentKey != null) {
                return false;
            }
        } else if (!parentKey.equals(other.parentKey)) {
            return false;
        }
        if (priority != other.priority) {
            return false;
        }
        if (serviceLevel == null) {
            if (other.serviceLevel != null) {
                return false;
            }
        } else if (!serviceLevel.equals(other.serviceLevel)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

}
