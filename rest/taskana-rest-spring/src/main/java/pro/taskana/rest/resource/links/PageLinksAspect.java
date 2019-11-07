package pro.taskana.rest.resource.links;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.PagedResources.PageMetadata;

/**
 * Implementation of the PageLinks annotation to generate HATEOAS Links for paged list resources.
 */
@Configuration
@Aspect
public class PageLinksAspect {

    @Around("@annotation(pro.taskana.rest.resource.links.PageLinks) && args(data, page, ..)")
    public ResourceSupport addLinksToPageResource(ProceedingJoinPoint joinPoint, List<?> data, PageMetadata page)
        throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
            .getRequest();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        PageLinks pageLinks = method.getAnnotation(PageLinks.class);
        String relativeUrl = pageLinks.value();
        UriComponentsBuilder original = originalUri(relativeUrl, request);
        ResourceSupport resourceSupport = (ResourceSupport) joinPoint.proceed();
        resourceSupport.add(new Link(original.toUriString()).withSelfRel());
        if (page != null) {
            resourceSupport.add(new Link(original.replaceQueryParam("page", 1).toUriString()).withRel(Link.REL_FIRST));
            resourceSupport.add(new Link(original.replaceQueryParam("page", page.getTotalPages()).toUriString())
                .withRel(Link.REL_LAST));
            if (page.getNumber() > 1) {
                resourceSupport
                    .add(new Link(original.replaceQueryParam("page", page.getNumber() - 1).toUriString())
                        .withRel(Link.REL_PREVIOUS));
            }
            if (page.getNumber() < page.getTotalPages()) {
                resourceSupport
                    .add(new Link(original.replaceQueryParam("page", page.getNumber() + 1).toUriString())
                        .withRel(Link.REL_NEXT));
            }
        }
        return resourceSupport;
    }

    private UriComponentsBuilder originalUri(String relativeUrl, HttpServletRequest request) {
        //any controller with a "root" mapping will do
        UriComponentsBuilder baseUri = linkTo(WorkbasketController.class).toUriComponentsBuilder();
        baseUri.path(relativeUrl);
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            for (String value : entry.getValue()) {
                baseUri.queryParam(entry.getKey(), value);
            }
        }
        return baseUri;
    }
}
