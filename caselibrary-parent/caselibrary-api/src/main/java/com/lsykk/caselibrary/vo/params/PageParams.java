package com.lsykk.caselibrary.vo.params;

import lombok.Data;

@Data
public class PageParams {
    // 默认的页面大小
    private int page = 1;

    private int pageSize = 10;

    public PageParams(Integer page, Integer pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }
}
