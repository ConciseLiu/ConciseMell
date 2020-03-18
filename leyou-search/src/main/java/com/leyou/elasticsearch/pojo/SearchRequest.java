package com.leyou.elasticsearch.pojo;

import com.sun.org.apache.xpath.internal.operations.Bool;

public class SearchRequest {
    private static final Integer DEFAULT_PAGE = 1;

    private static final Integer DEFAULT_SIZE = 5;

    private String key;

    private Integer page = DEFAULT_PAGE;

    private Integer size = DEFAULT_SIZE;

    private String sortBy = ""; // 排序字段

    private Boolean descending = true; // 是否降序

    public Integer getSize() {
        return size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Boolean getDescending() {
        return descending;
    }

    public void setDescending(Boolean descending) {
        this.descending = descending;
    }

    public void setSize(Integer size) {
        if (size == null) {
            size = DEFAULT_SIZE;
        }
        this.size = Math.max(size, DEFAULT_PAGE);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        if (page == null) {
            page = DEFAULT_PAGE;
        }
        this.page = Math.max(page, DEFAULT_PAGE);
    }

    public static Integer getDefaultPage() {
        return DEFAULT_PAGE;
    }

    public static Integer getDefaultSize() {
        return DEFAULT_SIZE;
    }
}
