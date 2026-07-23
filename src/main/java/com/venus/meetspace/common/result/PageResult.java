package com.venus.meetspace.common.result;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private List<T> records;

    private long total;

    private long page;

    private long size;

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
