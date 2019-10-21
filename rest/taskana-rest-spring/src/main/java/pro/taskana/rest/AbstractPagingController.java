package pro.taskana.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.MultiValueMap;

import pro.taskana.BaseQuery;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.rest.resource.PagedResources.PageMetadata;

/**
 * Abstract superclass for taskana REST controller with pageable resources.
 */
public abstract class AbstractPagingController {

    private static final String PAGING_PAGE = "page";
    private static final String PAGING_PAGE_SIZE = "page-size";

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

    protected PageMetadata getPageMetadata(MultiValueMap<String, String> params, BaseQuery<?, ?> query)
        throws InvalidArgumentException {
        PageMetadata pageMetadata = null;
        if (hasPagingInformationInParams(params)) {
            // paging
            long totalElements = query.count();
            pageMetadata = initPageMetadata(params, totalElements);
            validateNoInvalidParameterIsLeft(params);
        } else {
            // not paging
            validateNoInvalidParameterIsLeft(params);
        }
        return pageMetadata;
    }

    protected <T> List<T> getQueryList(BaseQuery<T, ?> query, PageMetadata pageMetadata) {
        List<T> resultList;
        if (pageMetadata != null) {
            resultList = query.listPage((int) pageMetadata.getNumber(), (int) pageMetadata.getSize());
        } else {
            resultList = query.list();
        }
        return resultList;
    }

    private boolean hasPagingInformationInParams(MultiValueMap<String, String> params) {
        return params.getFirst(PAGING_PAGE) != null;
    }

    protected PageMetadata initPageMetadata(MultiValueMap<String, String> param, long totalElements)
        throws InvalidArgumentException {
        long pageSize = getPageSize(param);
        long page = getPage(param);

        PageMetadata pageMetadata = new PageMetadata(pageSize, page,
            totalElements != 0 ? totalElements : Integer.MAX_VALUE);
        if (pageMetadata.getNumber() > pageMetadata.getTotalPages()) {
            // unfortunately no setter for number
            pageMetadata = new PageMetadata(pageSize, pageMetadata.getTotalPages(), totalElements);
        }
        return pageMetadata;
    }

    // This method is deprecated please remove it after updating taskana-simple-history reference to it.
    @Deprecated
    protected PageMetadata initPageMetadata(String pagesizeParam, String pageParam, long totalElements)
        throws InvalidArgumentException {
        long pageSize;
        long page;
        try {
            pageSize = Long.parseLong(pagesizeParam);
            page = Long.parseLong(pageParam);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("page and pageSize must be a integer value.", e.getCause());
        }
        PageMetadata pageMetadata = new PageMetadata(pageSize, page, totalElements);
        if (pageMetadata.getNumber() > pageMetadata.getTotalPages()) {
            // unfortunately no setter for number
            pageMetadata = new PageMetadata(pageSize, pageMetadata.getTotalPages(), totalElements);
        }
        return pageMetadata;
    }

    private long getPage(MultiValueMap<String, String> params) throws InvalidArgumentException {
        String param = params.getFirst(PAGING_PAGE);
        params.remove(PAGING_PAGE);
        try {
            return Long.parseLong(param != null ? param : "1");
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("page must be a integer value.", e.getCause());
        }
    }

    private long getPageSize(MultiValueMap<String, String> params) throws InvalidArgumentException {
        String param = params.getFirst(PAGING_PAGE_SIZE);
        params.remove(PAGING_PAGE_SIZE);
        try {
            return param != null ? Long.valueOf(param) : Integer.MAX_VALUE;
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("page-size must be a integer value.", e.getCause());
        }
    }

}
