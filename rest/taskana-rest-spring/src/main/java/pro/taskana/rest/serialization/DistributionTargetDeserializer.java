package pro.taskana.rest.serialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;

/**
 * This class deserializes the string list to real workbaskets.
 */
public class DistributionTargetDeserializer extends StdDeserializer<List<Workbasket>> {

    private static final long serialVersionUID = 4226950057149602129L;

    private static final Logger LOGGER = LoggerFactory.getLogger(DistributionTargetDeserializer.class);

    @Autowired
    private WorkbasketService workbasketService;

    public DistributionTargetDeserializer() {
        this(null);
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    public DistributionTargetDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public List<Workbasket> deserialize(JsonParser jsonparser, DeserializationContext context)
        throws IOException, JsonProcessingException {
        List<Workbasket> distributionTargets = new ArrayList<Workbasket>();
        while (jsonparser.nextToken() != JsonToken.END_ARRAY) {
            String id = jsonparser.getText();
            try {
                distributionTargets.add(workbasketService.getWorkbasket(id));
            } catch (WorkbasketNotFoundException e) {
                LOGGER.error("The workbasket with the id '" + id + "' is not found in database.");
            } catch (NotAuthorizedException e) {
                LOGGER.error(
                    "The user misses some required permissions for the workbasket with ID '" + id
                        + "'. Exception = {}.",
                    e);
            }
        }
        return distributionTargets;
    }
}
