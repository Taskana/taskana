package pro.taskana.rest;

import org.springframework.hateoas.PagedResources.PageMetadata;

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
            throw new InvalidArgumentException("page and pagesize must be a integer value.");
        }
        PageMetadata pageMetadata = new PageMetadata(pagesize, page, totalElements);
        return pageMetadata;
    }

}
