package com.epam.esm.page;

import com.epam.esm.model.Entity;
import com.epam.esm.service.InvalidPageException;
import com.epam.esm.service.PageOutOfBoundsException;
import com.epam.esm.service.ResourceNotFoundException;
import com.epam.esm.service.Service;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;

import java.util.List;

public interface LinksBuilder<T extends Entity> {
    T buildLinks(T entity) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException;

    CollectionModel<T> buildPageLinks(List<T> entities, int currentOffset, int currentLimit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException;
}
