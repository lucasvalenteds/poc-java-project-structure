package com.example.shared;

import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class ServiceResponseFilter<T extends ServiceResponseFilter<T>> {

    @Setter
    private Long page;
    @Setter
    private Long size;

    @Setter
    private String sort;
    @Setter
    private String sortDirection;

    @SuppressWarnings("unchecked")
    final T self() {
        return (T) this;
    }

    public Long getPage() {
        return page;
    }

    public T page(Long page) {
        this.page = page;
        return self();
    }

    public Long getSize() {
        return size;
    }

    public T size(Long size) {
        this.size = size;
        return self();
    }

    public String getSort() {
        return sort;
    }

    public T sort(String sort) {
        this.sort = sort;
        return self();
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public T sortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
        return self();
    }
}
