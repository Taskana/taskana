package pro.taskana.impl;

import java.time.Instant;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketSummary;
import pro.taskana.WorkbasketType;

/**
 * Workbasket entity.
 */
public class WorkbasketImpl implements Workbasket {

    private String id;
    private String key;
    private Instant created;
    private Instant modified;
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

    WorkbasketImpl() {
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
    public String getOwner() {
        return owner;
    }

    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public WorkbasketType getType() {
        return type;
    }

    @Override
    public void setType(WorkbasketType type) {
        this.type = type;
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
    public String getOrgLevel1() {
        return orgLevel1;
    }

    @Override
    public void setOrgLevel1(String orgLevel1) {
        this.orgLevel1 = orgLevel1;
    }

    @Override
    public String getOrgLevel2() {
        return orgLevel2;
    }

    @Override
    public void setOrgLevel2(String orgLevel2) {
        this.orgLevel2 = orgLevel2;
    }

    @Override
    public String getOrgLevel3() {
        return orgLevel3;
    }

    @Override
    public void setOrgLevel3(String orgLevel3) {
        this.orgLevel3 = orgLevel3;
    }

    @Override
    public String getOrgLevel4() {
        return orgLevel4;
    }

    @Override
    public void setOrgLevel4(String orgLevel4) {
        this.orgLevel4 = orgLevel4;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public WorkbasketSummary asSummary() {
        WorkbasketSummaryImpl result = new WorkbasketSummaryImpl();
        result.setId(this.getId());
        result.setKey(this.getKey());
        result.setName(this.getName());
        result.setDescription(this.getDescription());
        result.setOwner(this.getOwner());
        result.setDomain(this.getDomain());
        result.setType(this.getType());
        result.setOrgLevel1(this.getOrgLevel1());
        result.setOrgLevel2(this.getOrgLevel2());
        result.setOrgLevel3(this.getOrgLevel3());
        result.setOrgLevel4(this.getOrgLevel4());
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((custom1 == null) ? 0 : custom1.hashCode());
        result = prime * result + ((custom2 == null) ? 0 : custom2.hashCode());
        result = prime * result + ((custom3 == null) ? 0 : custom3.hashCode());
        result = prime * result + ((custom4 == null) ? 0 : custom4.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((domain == null) ? 0 : domain.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((modified == null) ? 0 : modified.hashCode());
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
        WorkbasketImpl other = (WorkbasketImpl) obj;
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
        builder.append("Workbasket [id=");
        builder.append(id);
        builder.append(", key=");
        builder.append(key);
        builder.append(", created=");
        builder.append(created);
        builder.append(", modified=");
        builder.append(modified);
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
        builder.append(", custom1=");
        builder.append(custom1);
        builder.append(", custom2=");
        builder.append(custom2);
        builder.append(", custom3=");
        builder.append(custom3);
        builder.append(", custom4=");
        builder.append(custom4);
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
