package pro.taskana.impl;

import pro.taskana.WorkbasketSummary;
import pro.taskana.WorkbasketType;

/**
 * This entity contains the most important information about a workbasket.
 */
public class WorkbasketSummaryImpl implements WorkbasketSummary {

    private String id;
    private String key;
    private String name;
    private String description;
    private String owner;
    private String domain;
    private WorkbasketType type;
    private String custom1;
    private String custom2;
    private String custom3;
    private String custom4;
    private String orgLevel1;
    private String orgLevel2;
    private String orgLevel3;
    private String orgLevel4;

    WorkbasketSummaryImpl() {
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketSummary#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketSummary#getKey()
     */
    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketSummary#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketSummary#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketSummary#getOwner()
     */
    @Override
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketSummary#getDomain()
     */
    @Override
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketSummary#getType()
     */
    @Override
    public WorkbasketType getType() {
        return type;
    }

    public void setType(WorkbasketType type) {
        this.type = type;
    }

    /*
     *  (non-Javadoc)
     *  @see pro.taskana.impl.WorkbasketSummary#getCustom1()
     */
    @Override
    public String getCustom1() {
        return custom1;
    }

    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }

    /*
     *  (non-Javadoc)
     *  @see pro.taskana.impl.WorkbasketSummary#getCustom2()
     */
    @Override
    public String getCustom2() {
        return custom2;
    }

    public void setCustom2(String custom2) {
        this.custom2 = custom2;
    }

    /*
     *  (non-Javadoc)
     *  @see pro.taskana.impl.WorkbasketSummary#getCustom3()
     */
    @Override
    public String getCustom3() {
        return custom3;
    }

    public void setCustom3(String custom3) {
        this.custom3 = custom3;
    }

    /*
     *  (non-Javadoc)
     *  @see pro.taskana.impl.WorkbasketSummary#getCustom4()
     */
    @Override
    public String getCustom4() {
        return custom4;
    }

    public void setCustom4(String custom4) {
        this.custom4 = custom4;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketSummary#getOrgLevel1()
     */
    @Override
    public String getOrgLevel1() {
        return orgLevel1;
    }

    public void setOrgLevel1(String orgLevel1) {
        this.orgLevel1 = orgLevel1;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketSummary#getOrgLevel2()
     */
    @Override
    public String getOrgLevel2() {
        return orgLevel2;
    }

    public void setOrgLevel2(String orgLevel2) {
        this.orgLevel2 = orgLevel2;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketSummary#getOrgLevel3()
     */
    @Override
    public String getOrgLevel3() {
        return orgLevel3;
    }

    public void setOrgLevel3(String orgLevel3) {
        this.orgLevel3 = orgLevel3;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketSummary#getOrgLevel4()
     */
    @Override
    public String getOrgLevel4() {
        return orgLevel4;
    }

    public void setOrgLevel4(String orgLevel4) {
        this.orgLevel4 = orgLevel4;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((domain == null) ? 0 : domain.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((orgLevel1 == null) ? 0 : orgLevel1.hashCode());
        result = prime * result + ((orgLevel2 == null) ? 0 : orgLevel2.hashCode());
        result = prime * result + ((orgLevel3 == null) ? 0 : orgLevel3.hashCode());
        result = prime * result + ((orgLevel4 == null) ? 0 : orgLevel4.hashCode());
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
        WorkbasketSummaryImpl other = (WorkbasketSummaryImpl) obj;
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
        if (orgLevel1 == null) {
            if (other.orgLevel1 != null) {
                return false;
            }
        } else if (!orgLevel1.equals(other.orgLevel1)) {
            return false;
        }
        if (orgLevel2 == null) {
            if (other.orgLevel2 != null) {
                return false;
            }
        } else if (!orgLevel2.equals(other.orgLevel2)) {
            return false;
        }
        if (orgLevel3 == null) {
            if (other.orgLevel3 != null) {
                return false;
            }
        } else if (!orgLevel3.equals(other.orgLevel3)) {
            return false;
        }
        if (orgLevel4 == null) {
            if (other.orgLevel4 != null) {
                return false;
            }
        } else if (!orgLevel4.equals(other.orgLevel4)) {
            return false;
        }
        if (owner == null) {
            if (other.owner != null) {
                return false;
            }
        } else if (!owner.equals(other.owner)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WorkbasketSummaryImpl [id=");
        builder.append(id);
        builder.append(", key=");
        builder.append(key);
        builder.append(", name=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", owner=");
        builder.append(owner);
        builder.append(", domain=");
        builder.append(domain);
        builder.append(", type=");
        builder.append(type);
        builder.append(", orgLevel1=");
        builder.append(orgLevel1);
        builder.append(", orgLevel2=");
        builder.append(orgLevel2);
        builder.append(", orgLevel3=");
        builder.append(orgLevel3);
        builder.append(", orgLevel4=");
        builder.append(orgLevel4);
        builder.append("]");
        return builder.toString();
    }

}
