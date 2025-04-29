package com.ltphat.task_management.application.dtos.shared;

import lombok.Data;
import java.util.List;

@Data
public class PagedResponseDto<T> {

    private List<T> items;
    private int currentPage;
    private int totalPages;
    private long totalItems;

    public PagedResponseDto(List<T> items, int currentPage, int totalPages, long totalItems) {
        this.items = items;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
    }

    public List<T> getContent() {
        return items;
    }
}