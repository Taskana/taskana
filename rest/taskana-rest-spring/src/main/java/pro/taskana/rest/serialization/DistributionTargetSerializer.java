package pro.taskana.rest.serialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import pro.taskana.Workbasket;

/**
 * This class serializes the distribution targets to an string array with ids.
 */
public class DistributionTargetSerializer extends StdSerializer<List<Workbasket>> {

    private static final long serialVersionUID = -4655804943794734821L;

    public DistributionTargetSerializer() {
        this(null);
    }

    public DistributionTargetSerializer(Class<List<Workbasket>> t) {
        super(t);
    }

    @Override
    public void serialize(List<Workbasket> workbaskets, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
        List<String> ids = new ArrayList<>();

        for (Workbasket item : workbaskets) {
            ids.add(item.getId());
        }
        gen.writeObject(ids);
    }
}
