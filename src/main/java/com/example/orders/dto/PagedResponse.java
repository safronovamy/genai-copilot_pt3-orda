package com.example.orders.dto;


import java.util.List;
import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;
@Getter
@Builder
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> items;
    private int page; // 1-based
    private int limit;
    private long totalItems;
    private int totalPages;
}

