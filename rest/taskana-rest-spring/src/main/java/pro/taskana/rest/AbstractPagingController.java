package pro.taskana.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.util.MultiValueMap;

import pro.taskana.exceptions.InvalidArgumentException;

/**
 * Abstract superclass for taskana REST controller with pagable resources.
 */
public abstract class AbstractPagingController {

    protected PageMetadata initPageMetadata(String pagesizeParam, String pageParam, long totalElements)
        throws InvalidArgumentException {
        long pagesize;
        long page;
        try {
            pagesize = Long.valueOf(pagesizeParam);
            page = Long.valueOf(pageParam);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("page and pagesize must be a integer value.", e.getCause());
        }
        PageMetadata pageMetadata = new PageMetadata(pagesize, page, totalElements);
        if (pageMetadata.getNumber() > pageMetadata.getTotalPages()) {
            // unfortunately no setter for number
            pageMetadata = new PageMetadata(pagesize, pageMetadata.getTotalPages(), totalElements);
        }
        return pageMetadata;
    }

    protected String[] extractCommaSeparatedFields(List<String> list) {
        List<String> values = new ArrayList<>();
        if (list != null) {
            list.forEach(item -> values.addAll(Arrays.asList(item.split(","))));
        }
        return values.toArray(new String[0]);
    }

    protected void validateNoInvalidParameterIsLeft(MultiValueMap<String, String> params)
        throws InvalidArgumentException {
        if (!params.isEmpty()) {
            throw new InvalidArgumentException("Invalid parameter specified: " + params.keySet());
        }
    }

}
