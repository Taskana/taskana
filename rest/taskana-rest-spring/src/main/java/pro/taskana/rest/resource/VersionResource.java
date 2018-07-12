package pro.taskana.rest.resource;

import org.springframework.hateoas.ResourceSupport;

/**
* Resource class for version information.
*/
public class VersionResource  extends ResourceSupport {

   private String version;

   public String getVersion() {
       return version;
   }

   public void setVersion(String version) {
       this.version = version;
   }
}
