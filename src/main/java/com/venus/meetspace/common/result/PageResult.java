package com.venus.meetspace.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Paginated response")
public class PageResult<T> {

    @Schema(description = "Data list")
    private List<T> records;

    @Schema(description = "Total records")
    private long total;

    @Schema(description = "Current page number (1-based)")
    private long page;

    @Schema(description = "Page size")
    private long size;

    @Schema(description = "Total pages")
    private long totalPages;

    public static <T> PageResult<T> of(List<T> records, long total, long page, long size) {
        PageResult<T> result = new PageResult<>();
        result.records = records;
        result.total = total;
        result.page = page;
        result.size = size;
        result.totalPages = size > 0 ? (total + size - 1) / size : 0;
        return result;
    }
}
