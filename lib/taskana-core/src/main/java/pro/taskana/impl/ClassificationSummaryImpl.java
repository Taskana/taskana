package pro.taskana.impl;

import pro.taskana.ClassificationSummary;

/**
 * Implementation for the short summaries of a classification entity.
 */
public class ClassificationSummaryImpl implements ClassificationSummary {

    private String id;
    private String key;
    private String category;
    private String type;
    private String domain;
    private String name;
    private String parentId;
    private String parentKey;
    private int priority;
    private String serviceLevel; // PddDThhHmmM
    private String custom1;
    private String custom2;
    private String custom3;
    private String custom4;
    private String custom5;
    private String custom6;
    private String custom7;
    private String custom8;

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String getServiceLevel() {
        return serviceLevel;
    }

    public void setServiceLevel(String serviceLevel) {
        this.serviceLevel = serviceLevel;
    }

    ClassificationSummaryImpl() {
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
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((domain == null) ? 0 : domain.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
        result = prime * result + ((parentKey == null) ? 0 : parentKey.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((custom1 == null) ? 0 : custom1.hashCode());
        result = prime * result + ((custom2 == null) ? 0 : custom2.hashCode());
        result = prime * result + ((custom3 == null) ? 0 : custom3.hashCode());
        result = prime * result + ((custom4 == null) ? 0 : custom4.hashCode());
        result = prime * result + ((custom5 == null) ? 0 : custom5.hashCode());
        result = prime * result + ((custom6 == null) ? 0 : custom6.hashCode());
        result = prime * result + ((custom7 == null) ? 0 : custom7.hashCode());
        result = prime * result + ((custom8 == null) ? 0 : custom8.hashCode());
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
        if (!(obj instanceof ClassificationSummaryImpl)) {
            return false;
        }
        ClassificationSummaryImpl other = (ClassificationSummaryImpl) obj;
        if (category == null) {
            if (other.category != null) {
                return false;
            }
        } else if (!category.equals(other.category)) {
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
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
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
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
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
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ClassificationSummaryImpl [id=");
        builder.append(id);
        builder.append(", key=");
        builder.append(key);
        builder.append(", category=");
        builder.append(category);
        builder.append(", type=");
        builder.append(type);
        builder.append(", domain=");
        builder.append(domain);
        builder.append(", name=");
        builder.append(name);
        builder.append(", parentId=");
        builder.append(parentId);
        builder.append(", parentKey=");
        builder.append(parentKey);
        builder.append(", priority=");
        builder.append(priority);
        builder.append(", serviceLevel=");
        builder.append(serviceLevel);
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

}
