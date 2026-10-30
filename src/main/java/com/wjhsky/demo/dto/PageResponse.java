package com.wjhsky.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 统一分页响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    /** 当前页码 (从0开始) */
    private int page;

    /** 每页大小 */
    private int size;

    /** 总记录数 */
    private long totalElements;

    /** 总页数 */
    private int totalPages;

    /** 是否为首页 */
    private boolean first;

    /** 是否为末页 */
    private boolean last;

    /** 数据列表 */
    private List<T> content;

    /**
     * 基于 Spring Data Page 构造分页响应
     */
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .content(page.getContent())
                .build();
    }
}
