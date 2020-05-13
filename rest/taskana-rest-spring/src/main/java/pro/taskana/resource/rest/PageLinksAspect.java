package pro.taskana.resource.rest;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Implementation of the PageLinks annotation to generate HATEOAS Links for paged list resources.
 */
@Configuration
@Aspect
public class PageLinksAspect {

  @SuppressWarnings("unchecked")
  @Around("@annotation(pro.taskana.resource.rest.PageLinks) && args(data, page, ..)")
  public <T extends RepresentationModel<? extends T> & ProceedingJoinPoint>
      RepresentationModel<T> addLinksToPageResource(
          ProceedingJoinPoint joinPoint, List<?> data, PageMetadata page) throws Throwable {
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    PageLinks pageLinks = method.getAnnotation(PageLinks.class);
    String relativeUrl = pageLinks.value();
    UriComponentsBuilder original = originalUri(relativeUrl, request);
    RepresentationModel<T> resourceSupport = (RepresentationModel<T>) joinPoint.proceed();
    if (page != null) {
      resourceSupport.add(
           Link.of(original.replaceQueryParam("page", page.getNumber()).toUriString())
              .withSelfRel());
      resourceSupport.add(
          Link.of(original.replaceQueryParam("page", 1).toUriString())
              .withRel(IanaLinkRelations.FIRST));
      resourceSupport.add(
          Link.of(original.replaceQueryParam("page", page.getTotalPages()).toUriString())
              .withRel(IanaLinkRelations.LAST));
      if (page.getNumber() > 1) {
        resourceSupport.add(
            Link.of(original.replaceQueryParam("page", page.getNumber() - 1).toUriString())
                .withRel(IanaLinkRelations.PREV));
      }
      if (page.getNumber() < page.getTotalPages()) {
        resourceSupport.add(
            Link.of(original.replaceQueryParam("page", page.getNumber() + 1).toUriString())
                .withRel(IanaLinkRelations.NEXT));
      }
    } else {
      resourceSupport.add(Link.of(original.toUriString()).withSelfRel());
    }
    return resourceSupport;
  }

  private UriComponentsBuilder originalUri(String relativeUrl, HttpServletRequest request) {
    // argument to linkTo does not matter as we just want to have the default baseUrl
    UriComponentsBuilder baseUri = linkTo(PageLinksAspect.class).toUriComponentsBuilder();
    baseUri.path(relativeUrl);
    for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
      for (String value : entry.getValue()) {
        baseUri.queryParam(entry.getKey(), value);
      }
    }
    return baseUri;
  }
}
