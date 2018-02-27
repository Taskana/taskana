package pro.taskana.rest.serialization;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import pro.taskana.Workbasket;

/**
 * This class is used to override the distributiontargets with non standard
 * serialization classes.
 */
public abstract class WorkbasketMixIn {

    @JsonSerialize(using = DistributionTargetSerializer.class)
    @JsonDeserialize(using = DistributionTargetDeserializer.class)
    abstract List<Workbasket> getDistributionTargets();

}
