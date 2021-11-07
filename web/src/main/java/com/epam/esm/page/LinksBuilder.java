package com.epam.esm.page;

import com.epam.esm.model.Entity;
import com.epam.esm.service.InvalidPageException;
import com.epam.esm.service.PageOutOfBoundsException;
import com.epam.esm.service.ResourceNotFoundException;
import com.epam.esm.service.Service;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;

import java.util.List;

/**
 * Entity links builder that makes entity links
 * and entity page links
 * @param <T>
 */
public interface LinksBuilder<T extends Entity> {
    /**
     * builds links for passed entity
     * @param entity entity to build links
     * @return entity that has  built links
     * @throws ResourceNotFoundException if entity is not found
     * @throws PageOutOfBoundsException if offset if greater then total elements
     * @throws InvalidPageException if offset or limit is negative
     */
    T buildLinks(T entity) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException;

    /**
     * Builds links for passed entity page
     * @param entities entities to build links
     * @param currentOffset current page offset
     * @param currentLimit current page limit
     * @return entities that have build links
     * @throws ResourceNotFoundException if entity is not found
     * @throws PageOutOfBoundsException if offset is greater then total elements
     * @throws InvalidPageException if offset or limit is negative
     */
    PagedModel<T> buildPageLinks(List<T> entities, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException;
}
