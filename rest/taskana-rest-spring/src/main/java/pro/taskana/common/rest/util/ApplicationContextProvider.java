package pro.taskana.common.rest.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider {
  private static ApplicationContext context;

  public static ApplicationContext getApplicationContext() {
    return context;
  }

  @Autowired
  public void setContext(ApplicationContext context) {
    ApplicationContextProvider.context = context;
  }
}
