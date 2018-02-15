package pro.taskana.impl;

import pro.taskana.Attachment;

/**
 * This class contains helper methods for Junit Tests.
 *
 * @author bbr
 */
public final class JunitHelper {

    private JunitHelper() {

    }

    public static ObjectReference createDefaultObjRef() {
        return createObjRef("company", "system", "instance", "type", "value");
    }

    public static ObjectReference createObjRef(String company, String system, String instance, String type,
        String value) {
        ObjectReference objRef = new ObjectReference();
        objRef.setCompany(company);
        objRef.setSystem(system);
        objRef.setSystemInstance(instance);
        objRef.setType(type);
        objRef.setValue(value);
        return objRef;
    }

    public static Attachment createDefaultAttachment() {
        return createAttachment("TAI:000", "CHANNEL", createDefaultObjRef());
    }

    public static Attachment createAttachment(String id, String channel, ObjectReference objectReference) {
        AttachmentImpl attachment = new AttachmentImpl();
        attachment.setChannel(channel);
        attachment.setId(id);
        attachment.setObjectReference(objectReference);
        return attachment;
    }
}
