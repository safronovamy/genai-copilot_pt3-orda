package com.example.orders.dto;

// Create a generic PagedResponse<T> DTO for paginated results.
// Fields:
// - List<T> items
// - int page  (IMPORTANT: 1-based page number returned to client)
// - int limit
// - long totalItems
// - int totalPages
//
// Requirements:
// - Provide constructors or builder.
// - Ensure JSON serialization is straightforward.
// - Package: com.example.orders.dto
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

