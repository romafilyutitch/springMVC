package com.epam.esm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;

public class OffsetPageable implements Pageable {

    private final int offset;
    private final int limit;
    private final Sort sort;

    public OffsetPageable(int offset, int limit, Sort sort) {
        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
    }

    public OffsetPageable(int offset, int limit) {
        this(offset, limit, Sort.unsorted());
    }

    @Override
    public int getPageNumber() {
        int number = offset / limit + 1;
        number = offset % limit == 0 ? number : ++number;
        return number;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetPageable(offset + limit, limit);
    }

    @Override
    public Pageable previousOrFirst() {
        return new OffsetPageable(offset - limit, limit);
    }

    @Override
    public Pageable first() {
        return new OffsetPageable(0, limit);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new OffsetPageable(pageNumber * limit, limit);
    }

    @Override
    public boolean hasPrevious() {
        return offset > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OffsetPageable that = (OffsetPageable) o;
        return offset == that.offset && limit == that.limit && Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, limit, sort);
    }


}
